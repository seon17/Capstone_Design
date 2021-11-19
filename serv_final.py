import socket
import numpy as np
import cv2
import threading
import mediapipe as mp
import math
import warnings
import os

from queue import Queue

os.environ['GLOG_minloglevel'] = '3'
warnings.simplefilter("ignore", DeprecationWarning)

list = []

## 우분투 서버
host = 'ec2-3-134-85-20.us-east-2.compute.amazonaws.com'
port = 8080

## 구토 발생 플래그
vomit = False

## MEDIAPIPE
mp_drawing = mp.solutions.drawing_utils
mp_holistic = mp.solutions.holistic

## 클라이언트 그룹
group = []
## 전송 큐
q = Queue()

## 스레드 락
lock = threading.Lock()


## 서버
class server:

    def __init__(self):
        self.auto = True
        self.data = np.ndarray(shape=(1, 224, 224, 3), dtype=np.float32)

    def run_server(self):
        client_sock, addr = server_sock.accept()
        group.append(client_sock)

        if client_sock:  # client_sock 가 null 값이 아니라면 (연결 승인 되었다면)
            print('Connected by?!', addr)  # 연결주소 print

        return client_sock

    def app_send(self, client_sock, group):
        global vomit
        if client_sock == group[1]: #수신 대상이 앱인 경우(구토 확인 데이터)
            while True:
                recv_data = client_sock.recv(1024).decode('utf-8') #앱에서 주기적으로 구토 확인 데이터 수신
                if vomit == True: #구토 발생했을 때
                    group[1].send("yes".encode('utf-8'))
                    vomit = False
                    print("------>구토 데이터 APP으로 전송")
                    print("구토 변수 초기화 완료 :", vomit, '\n')
                else: #구토 발생하지 않았을 때
                    group[1].send("no".encode('utf-8'))

    def pi_send(self, group, q):
        while True:
            recv = q.get()
            msg = recv[0]
            group[0].send(msg.encode('utf-8'))
            print('------>요람으로 데이터 전송\n')

    def app_recv(self, client_sock, q):
        if client_sock != group[0] and client_sock != group[1]: #수신 대상이 앱인 경우(제어 데이터)
            while True:
                in_data = client_sock.recv(1024).decode('utf-8')  #안드로이드에서 data 받음
                if len(in_data) > 0:
                    print('<------APP 데이터 수신 :', in_data, '\n')  #전송 받은값 디코딩
                    q.put([in_data, client_sock])
                else:
                    break

    #어깨 사이 거리 계산
    def dis(self, l_shoulder, r_shoulder):
        a = l_shoulder[0] - r_shoulder[0]
        b = l_shoulder[1] - r_shoulder[1]
        print('자세 인식중....(왼쪽 어깨 위치:', a, ', 오른쪽 어깨 위치:', b,')')

        return math.sqrt(math.pow(a, 2) + math.pow(b, 2))


    def pi_recv(self, client_sock, q):
        global vomit
        global count
        if client_sock == group[0]: #수신 대상이 요람인 경우
            while True:
                length = self.recvall(client_sock, 16)
                if length[0] == 50 and length[1] == 32: #구토 감지 데이터 수신 시
                    print('----------!!구 토 발 생!!----------\n')
                    vomit = True
                    continue

                stringData = self.recvall(client_sock, int(length))
                data1 = np.frombuffer(stringData, dtype='uint8')

                image = cv2.imdecode(data1, cv2.IMREAD_COLOR)

                print('<------이미지 수신(', length, len(length), ')')

                with mp_holistic.Holistic(
                        static_image_mode=True,
                        model_complexity=2) as holistic:

                    image_height, image_width, _ = image.shape
                    results = holistic.process(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))

                    try: #신체 부위들의 픽셀 위치
                        landmarks = results.pose_landmarks.landmark
                        l_shoulder = [landmarks[mp_holistic.PoseLandmark.LEFT_SHOULDER].x * image_width,
                                      landmarks[mp_holistic.PoseLandmark.LEFT_SHOULDER].y * image_height]
                        r_shoulder = [landmarks[mp_holistic.PoseLandmark.RIGHT_SHOULDER].x * image_width,
                                      landmarks[mp_holistic.PoseLandmark.RIGHT_SHOULDER].y * image_height]
                        l_ear = landmarks[mp_holistic.PoseLandmark.LEFT_EAR].y

                        r_ear = landmarks[mp_holistic.PoseLandmark.RIGHT_EAR].y
                        nose = landmarks[mp_holistic.PoseLandmark.NOSE].y

                        l_wrist = landmarks[mp_holistic.PoseLandmark.LEFT_WRIST].y
                        r_wrist = landmarks[mp_holistic.PoseLandmark.RIGHT_WRIST].y

                        l_pinky = landmarks[mp_holistic.PoseLandmark.LEFT_PINKY].y
                        r_pinky = landmarks[mp_holistic.PoseLandmark.RIGHT_PINKY].y

                        l_index = landmarks[mp_holistic.PoseLandmark.LEFT_INDEX].y
                        r_pinky = landmarks[mp_holistic.PoseLandmark.RIGHT_PINKY].y
                        l_hip = landmarks[mp_holistic.PoseLandmark.LEFT_HIP].y
                        r_hip = landmarks[mp_holistic.PoseLandmark.RIGHT_HIP].y

                        dist = self.dis(l_shoulder, r_shoulder) #어깨 사이 거리 계산
                        print('자세 인식 완료! (어깨 사이 거리 :', dist, ')')
                        if dist > 49.8: #정자세
                            list.append('x')
                            if list[-1] == list[-2]:
                                in_data = "x"
                                q.put([in_data, client_sock])
                                print('자세 인식 결과 : 정상')
                                list.clear()

                        else: #코와 귀 위치로 자세 판단
                            if nose < l_ear: #오른쪽 자세
                                in_data = "y"
                                q.put([in_data, client_sock])
                                print('자세 인식 결과 : 오른쪽')
                            if nose > r_ear: #왼쪽 자세
                                in_data = "z"
                                q.put(in_data, client_sock)
                                print('자세 인식 결과 : 왼쪽')

                    except: #신체가 인식되지 않는 경우
                        print("요람에 아기 없음")

                    annotated_image = image.copy()
                    mp_drawing.draw_landmarks(
                        annotated_image, results.pose_landmarks, mp_holistic.POSE_CONNECTIONS)
                    image = cv2.resize(annotated_image, dsize=(224, 224), interpolation=cv2.INTER_AREA)
                    cv2.imshow('a', image)
                    cv2.waitKey(1)


    #이미지 수신에 사용
    def recvall(self, client_sock, count):
        buf = b''
        while count:
            newbuf = client_sock.recv(count)
            if not newbuf: return None
            buf += newbuf
            count -= len(newbuf)
        return buf


if __name__ == '__main__':
    s = server()

    server_sock = socket.socket(socket.AF_INET)
    server_sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_sock.bind((host, port))
    server_sock.listen(1)
    print("기다리는 중..")

    while (1):
        client_sock = s.run_server()

        try:
            thread1 = threading.Thread(target=s.pi_recv, args=(client_sock, q,))
            if len(group) >= 1:
                thread1.start()

            thread2 = threading.Thread(target=s.app_send, args=(client_sock, group,))
            if len(group) >= 2:
                thread2.start()

            thread3 = threading.Thread(target=s.app_recv, args=(client_sock, q,))
            if len(group) >= 3:
                thread3.start()

            thread4 = threading.Thread(target=s.pi_send, args=(group, q,))
            thread4.start()

        except KeyboardInterrupt:
            client_sock.close()
            server_sock.close()
            break