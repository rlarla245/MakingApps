package com.updatetest.kotlintest_20190129

class Human {
    var country : String? = "대한민국"
    var name : String? = "김두현"
    var gender : String? = "남"
    var phoneNumber : String? = "01050043106"

    fun printHumanInfo(country : String, name : String, gender : String, phoneNumber : String) {
        println("${country}에 사는 ${name}의 성별은 ${gender}로, 휴대폰 번호는 ${phoneNumber}입니다.")
    }
}