# -*- coding: utf8 -*-
import socket
import cv2
import numpy as np
import threading
import os
import math

from time import sleep
from scipy.interpolate import griddata
from colour import Color
from scipy import interpolate

import busio
import board
import adafruit_amg88xx
import pigpio
import pygame

pi = pigpio.pi()

img_ok = True  #이미지 보낼건지
vomit = False
vomit_detect = False
detect = False

stop = False  #자동 인식 중지 여부

g_count = 0
j_count = 0
middle = 75

pin1 = 17  #서보 모터

lock = threading.Lock()

i2c_bus = busio.I2C(board.SCL, board.SDA)
os.putenv("SDL_FBDEV", "/dev/fb1")
sensor = adafruit_amg88xx.AMG88XX(i2c_bus)


class servo:

    def __init__(self):
        self.encode_param = [int(cv2.IMWRITE_JPEG_QUALITY), 90]
        self.recv_data = ''
        self.TIME = 0
        self.start = 0
        self.count = 0
        self.t = 0  # 타이머 시간 측정 변수
        self.tf = False  # 타이머 동작 유무
        self.bk = False  # 타이머 없앨건지 유무
        self.p_count = 0

        self.vol = 5  #음악 기본 볼륨 크기
        self.mpfile = 'music0'  #음악 파일명 초기화

    def img_send(self, client_sock):
        global stop
        while True:
            if stop==False:  #자동 인식 가동 중이면

                global g_count
                global j_count
                global vomit
                global vomit_detect
                global img_ok
                a = img_ok
                if a == True:
                    if (self.count == 0):  #첫 연결이면 연결 시간 필요
                        print("연결  대기")
                        sleep(10)
                        self.count = self.count + 1

                    elif self.count != 0 and vomit == False:  #첫 연결 아니고 구토 감지 없으면
                        # 3초마다 한 프레임 읽기
                        sleep(3)
                        cam = cv2.VideoCapture("http://172.20.10.7:8090/?action=stream")  #mjpg스트리밍 주소
                        cam.set(3, 224)
                        cam.set(4, 224)
                        # 성공 : ret = True, 실패 : ret = False
                        ret, frame = cam.read()
                        # encode_param의 형식으로 frame을 png로 이미지를 인코딩한다.
                        result, frame = cv2.imencode('.png', frame, self.encode_param)
                        # tobytes 변환
                        data = np.array(frame)
                        stringData = data.tobytes()

                        # 서버에 데이터 전송 (길이 16으로 고정)
                        client_sock.sendall((str(len(stringData))).encode().ljust(16) + stringData)
                        print('------> 이미지 전송\n')
                else:
                    pass

                # 구토 발생 시 서버에 데이터 전송
                if vomit_detect == True:
                    dummmy = "HI"  # 통신 구분자
                    j_count = j_count + 1  # 구토 딜레이

                    if j_count == 1:
                        print(" -----!!구   토   발  생!!-----\n")
                        vomit = True
                        img_ok = False

                        print("이미지 전송: {0}".format(img_ok))
                        print("구토 감지: {0}\n".format(vomit))
                        # 작동 중인 타이머가 있으면 초기화
                        if self.tf == True:
                            self.bk = True
                            self.t = 0

                        self.recv_data = 'n'
                        client_sock.sendall((str(len(dummmy))).encode().ljust(16))
                        print("------>구토 감지 데이터 전송\n")


    # 전동 타이머
    def timer(self):
        lock.acquire()
        time = threading.Timer(1, self.timer)
        self.t += 1
        self.tf = True

        if self.bk == True:
            time.cancel()
            self.t = 0
            self.tf = False
            self.bk = False
            print("타이머 중지됨\n")
            self.p_count = 0

        print('timer :', self.t)

        time.start()

        if self.t == self.TIME:
            global img_ok
            print('time out!!')
            img_ok = True
            time.cancel()
            self.t = 0
            self.tf = False
            self.recv_data = 'n'
            print("이미지 전송: {0}\n".format(img_ok))
            self.p_count = 0
        lock.release()

    def recv(self, client_sock):
        while True:
            global img_ok
            global g_count
            global vomit
            global j_count
            global vomit_detect
            global stop
            new_data = client_sock.recv(1024).decode()  # Server -> Client 데이터 수신

            if '!' in new_data:  #자동 인식 중지
                stop = True
            if '@' in new_data:  #자동 인식 재가동
                stop = False
                print("dh:",stop)
            if 'q' in new_data:  #수동 진동 종료
                if self.tf == True:  # 타이머(진동) 동작 중이면 진동 종료
                    sleep(1)
                    self.bk = True
                    self.t = 0
                img_ok = True

            if 'A' in new_data or 'B' in new_data or 'C' in new_data:  #진동 제어
                self.recv_data = new_data
                print('<------진동 제어 데이터 수신:', self.recv_data)

                img_ok = False  #이미지 전송 중지
                print("이미지 전송: {0}\n".format(img_ok))

                if '1' in self.recv_data:  #데이터의 시간 대로 타이머 설정
                    if self.tf == True:
                        self.bk = True
                        self.t = 0

                    self.TIME = 10
                    print("타이머 시간: {0}\n".format(self.TIME))

                    self.timer()

                elif '2' in self.recv_data:  #데이터의 시간 대로 타이머 설정
                    if self.tf == True:
                        self.bk = True
                        self.t = 0

                    self.TIME = 20
                    print("타이머 시간: {0}\n".format(self.TIME))

                    self.timer()
                else:
                    pass

            if 'OK' in new_data:  #앱으로부터 온 구토 알림 확인 데이터
                print('<------구토 알림 확인 데이터 수신:', new_data, '\n')
                img_ok = True
                g_count = 0
                j_count = 0
                vomit = False
                vomit_detect = False
                print("이미지 전송: {0}".format(img_ok))
                print("구토 감지: {0}\n".format(vomit))

            if 'music' in new_data:  #음악 재생
                img_ok = True
                if self.mpfile == "music0":
                    # init
                    pygame.mixer.init()

                    self.mpfile = "/home/pi/music" + new_data[7] + ".mp3"  #재생할 파일명

                    print(self.mpfile)

                    # load file
                    pygame.mixer.music.load(self.mpfile)

                    pygame.mixer.music.set_volume(0.5)

                    # play, 무한 반복
                    pygame.mixer.music.play(-1)

                elif 'vol' in new_data:  #음악 볼륨
                    if '+' in new_data:
                        vol = pygame.mixer.music.get_volume()
                        pygame.mixer.music.set_volume(vol + 0.3)

                    if '-' in new_data:
                        vol = pygame.mixer.music.get_volume()
                        pygame.mixer.music.set_volume(vol - 0.3)

                elif 'pause' in new_data:
                    if 'un' in new_data:  #일시 중지 된 음악 재생
                        pygame.mixer.music.unpause()
                    else:  #음악 일시 중지
                        pygame.mixer.music.pause()

                elif 'stop' in new_data:  #음악 중지
                    pygame.mixer.music.stop()
                    self.mpfile = "music0"

                elif 'prev' in new_data or 'next' in new_data:
                    pygame.mixer.music.stop()

                    if 'prev' in new_data:  #이전 곡의 파일명 설정
                        if self.mpfile[14] == '1':
                            self.mpfile = "/home/pi/music6.mp3"
                        else:
                            self.mpfile = "/home/pi/music" + str(int(self.mpfile[14]) - 1) + ".mp3"
                    else:  #다음 곡의 파일명 설정
                        if self.mpfile[14] == '6':
                            self.mpfile = "/home/pi/music1.mp3"
                            pass
                        else:
                            self.mpfile = "/home/pi/music" + str(int(self.mpfile[14]) + 1) + ".mp3"

                    # load file
                    pygame.mixer.music.load(self.mpfile)

                    # play, 무한 반복
                    pygame.mixer.music.play(-1)

            else:  # 이미지값, x y z
                if img_ok == True:
                    self.recv_data = new_data
                    print('<------자세 인식 데이터 수신(', self.recv_data, ')\n')


    def setServoPos(self, degree):
        # 각도는 180도를 넘을 수 없다.
        if degree > 180:
            degree = 180

        # 각도(degree)를 duty로 변경
        duty = 600 + (10 * degree)

        # 변경된 duty값을 서보 모터에 적용
        pi.set_servo_pulsewidth(17, duty)

    # 서보를 특정 각도로 움직였다 되돌아오게 하는 함수
    def servoSlowGoBack(self, degree, time):
        if (degree < middle):
            for x in range(middle, degree, -1):
                self.setServoPos(x)
                sleep(time)
            for x in range(degree, middle, 1):
                self.setServoPos(x)
                sleep(time)
        elif (degree > middle):
            for x in range(middle, degree, 1):
                self.setServoPos(x)
                sleep(time)
            for x in range(degree, middle, -1):
                self.setServoPos(x)
                sleep(time)
        else:
            self.setServoPos(middle)
            sleep(time)


class Vomit:
    sleep(2)
    def __init__(self):

        # 최저 온도
        self.MINTEMP = 18
        # 최고 온도
        self.MAXTEMP = 30  # 보간 색 수
        self.COLORDEPTH = 1024

        # 색상 보간 행렬
        self.points = [(math.floor(ix / 8), (ix % 8)) for ix in range(0, 64)]
        self.grid_x, self.grid_y = np.mgrid[0:7:32j, 0:7:32j]

        # 색 설정
        self.blue = Color("red")
        self.colors = list(self.blue.range_to(Color("blue"), self.COLORDEPTH))
        self.colors = [(int(c.red * 255), int(c.green * 255), int(c.blue * 255)) for c in self.colors]

        # 센서 픽셀 정의
        self.pix_res = (8, 8)
        self.xx, self.yy = (np.linspace(0, self.pix_res[0], self.pix_res[0]),
                            np.linspace(0, self.pix_res[1], self.pix_res[1]))

        self.zz = np.zeros(self.pix_res)
        # 보간 계수 및 온도 보간 행렬
        self.pix_mult = 4
        self.interp_res = (int(self.pix_mult * self.pix_res[0]),
                           int(self.pix_mult * self.pix_res[1]))

        self.grid_xx, self.grid_yy = (np.linspace(0, self.pix_res[0], self.interp_res[0]),
                                      np.linspace(0, self.pix_res[1], self.interp_res[1]))

        self.pixels = []
        self.pixels2 = []

        # 레이블링 된 객체들의 중앙 픽셀의 온도
        self.max_temp = 0
        # max_temp의  라벨링 번호(인덱스)
        self.max_temp_index = 0

    # 온도 보간 함수
    def interp(self, z_var):

        f = interpolate.interp2d(self.xx, self.yy, z_var, kind='cubic')
        return f(self.grid_xx, self.grid_yy)

    # 이진화 함수   -- 안씀 임계치 함수로 대체
    def pixels_bi(self, pixels):
        num = []
        for sensor in pixels:
            if sensor < 23:
                num.append(0)
            else:
                num.append(sensor)
        return num

    # 색상 변환 함수
    def constrain(self, val, min_val, max_val):
        return min(max_val, max(min_val, val))

    # 색상 보간 변환 함수
    def map_value(self, x, in_min, in_max, out_min, out_max):
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min

    def motion(self):
        global vomit
        global vomit_detect
        global g_count

        global detect
        while True:
            if stop==False:
                sleep(1)
                colorx = []  # 온도 -> 색상 변환 리스트
                
                for row in sensor.pixels:
                    self.pixels = self.pixels + row
                    self.pixels2 = self.pixels2 + row

                s = np.array(self.pixels, np.uint8)
                s = np.reshape(s, (8, 8))
                s = s[4:7,3:6]

                for i in s:
                    if detect == True:
                        detect = False
                        break
                    for j in i:
                        if j > 32:
                            g_count = g_count + 1
                            print("슬립", g_count)
                            if g_count > 0:
                                detect = True
                                vomit_detect = True
                            break

                self.pixels.clear()


# TCP Client
if __name__ == '__main__':
    s = servo()
    v = Vomit()
    client_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  # TCP Socket
    Host = 'ec2-3-134-85-20.us-east-2.compute.amazonaws.com'  # 통신할 대상의 IP 주소
    Port = 8080  # 통신할 대상의 Port 주소
    client_sock.connect((Host, Port))  # 서버로 연결시도
    print('Connecting to ', Host, Port)

    thread1 = threading.Thread(target=s.img_send, args=(client_sock,))
    thread1.start()

    thread2 = threading.Thread(target=s.recv, args=(client_sock,))
    thread2.start()

    thread3 = threading.Thread(target=v.motion, args=())
    thread3.start()

    thread4 = threading.Thread(target=s.recv, args=(client_sock,))
    thread4.start()

    while True:
        try:
            if 'A1' in s.recv_data:
                s.p_count = s.p_count + 1
                if s.p_count == 1:
                    print("강도 1 : 10초 ", s.recv_data)
                # a강도, 10초
                s.servoSlowGoBack(middle+5, 0.05)
                s.servoSlowGoBack(middle-5, 0.05)
            elif 'A2' in s.recv_data:
                s.p_count = s.p_count + 1
                if s.p_count == 1:
                    print("강도 1 : 20초 ", s.recv_data)
                # a강도, 20초
                s.servoSlowGoBack(middle+5, 0.05)
                s.servoSlowGoBack(middle-5, 0.05)
            elif 'B1' in s.recv_data:
                s.p_count = s.p_count + 1
                if s.p_count == 1:
                    print("강도 2 : 10초", s.recv_data)
                # b강도, 10초
                s.servoSlowGoBack(middle+5, 0.035)
                s.servoSlowGoBack(middle-5, 0.035)
            elif 'B2' in s.recv_data:
                print("강도 2 : 20초", s.recv_data)
                # b강도, 20초
                s.servoSlowGoBack(middle+5, 0.035)
                s.servoSlowGoBack(middle-5, 0.035)
            elif 'C1' in s.recv_data:
                print("강도 3 : 10초", s.recv_data)
                # c강도, 10초
                s.servoSlowGoBack(middle+5, 0.02)
                s.servoSlowGoBack(middle-5, 0.02)
            elif 'C2' in s.recv_data:
                print("강도 3 : 20초", s.recv_data)
                # c강도, 20초
                s.servoSlowGoBack(middle+5, 0.02)
                s.servoSlowGoBack(middle-5, 0.02)
            else:
                if 'x' in s.recv_data:
                    # 정자세, 모터 동작 X
                    pass
                elif 'y' in s.recv_data:
                    print("모터 동작 : 왼 쪽\n")
                    # 왼쪽으로 움직이게
                    s.recv_data = 'n'
                    s.servoSlowGoBack(middle-30, 0.05)
                    pass
                elif 'z' in s.recv_data:
                    print("모터 동작 : 오른쪽\n")
                    # 오른쪽으로 움직이게
                    s.recv_data = 'n'
                    s.servoSlowGoBack(middle+30, 0.05)
                    pass
                else:
                    pass

        except KeyboardInterrupt:
            client_sock.close()
            GPIO.cleanup()
            break