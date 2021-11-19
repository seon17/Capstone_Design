package com.example.babycareapp

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application(){

    override fun onCreate(){
        super.onCreate()
        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfig)
    }
}

//realm db 사용 시 필요