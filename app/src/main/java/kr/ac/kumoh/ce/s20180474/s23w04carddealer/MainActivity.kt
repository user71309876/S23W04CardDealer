package kr.ac.kumoh.ce.s20180474.s23w04carddealer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.ac.kumoh.ce.s20180474.s23w04carddealer.databinding.ActivityMainBinding
import java.util.Stack

class MainActivity : AppCompatActivity() {
    private lateinit var main:ActivityMainBinding
    private lateinit var model:CardViewModel
    private lateinit var card: Array<ImageView?>
    override fun onCreate(savedInstanceState: Bundle?) {
        var result:String
        super.onCreate(savedInstanceState)
        main=ActivityMainBinding.inflate(layoutInflater)
        setContentView(main.root)
        card=arrayOf(main.card1,main.card2,main.card3,main.card4,main.card5)
        model=ViewModelProvider(this)[CardViewModel::class.java]
        model.cards.observe(this,Observer{
            card.forEachIndexed{index, imageView ->
                imageView?.setImageResource(
                    resources.getIdentifier(
                        getCardName(it[index]),
                        "drawable",
                        packageName))}})
        main.btnShuffle.setOnClickListener {
            model.shuffle()
            result=getHand(model.cards.value!!)
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCardName(c:Int):String{
        var shape=when(c%4){
            -1->"joker"
            0->"spades"
            1->"diamonds"
            2->"hearts"
            3->"clubs"
            else->"error"
        }
        val number=when(c/4){
            0->"ace"
            in 1..9->(c/4+1).toString()
            10->"jack"
            11->"queen"
            12->"king"
            else ->"error"
        }
        if(shape in "joker")
            return "c_black_joker"
        if(number in arrayOf("jack","queen","king"))
            shape="${shape}2"
        return "c_${number}_of_${shape}"
    }

    //카드가 연속된 숫자인 경우
    private fun isOrder(c:IntArray):Int{
        val temp=c[0]/4
        for(i in 1..4){
            if(temp!=c[i]/4-i)
                return 0
        }
        return 1
    }
    //카드의 조합이 마운틴인 경우
    private fun isMouOrder(c:IntArray):Int{
        if(c[0]/4!=0)
            return 0
        val temp=9
        for(i in 1..4){
            if(temp+i-1!=c[i]/4)
                return 0
        }
        return 1
    }
    //카드의 모양이 같은 경우
    private fun isSame(c:IntArray):Int{
        val temp=c[0]%4
        for(i in 1..4){
            if(temp!=c[i]%4)
                return 0
        }
        return 1
    }
    //카드에 A가 있는 경우
    private fun isA(c:IntArray):Int{
        for(i in 0..4){
            if(c[i]/4==0)
                return 1
        }
        return 0
    }

    //크게 페어인 경우와 스트레이트인 경우로 분리하여 계산
    private fun getHand(c:IntArray):String{
        var handName="탑${c[4]/4+1}"
        var temp:Int
        var count=1
        val tempArray= Stack<Int>()
        val order=isOrder(c)//1씩 증가하는가(마운틴 제외)
        val same=isSame(c)//같은 모양인가
        val mouOrder=isMouOrder(c)//마운틴인가
        val hasA=isA(c)//A가 포함되어있는가
        val result="$order$same$hasA$mouOrder"
        Log.i("check",result)

        //위의 4가지 경우를 binary 형식으로 합쳐서 계산
        when(result){
            "1000"->handName="스트레이트"
            "1100"->handName="스트레이트 플러시"
            "1010"->handName="백 스트레이트"
            "1110"->handName="백 스트레이트 플러쉬"
            "0011"->handName="마운틴"
            "0111"->handName="로얄 스트레이트 플러쉬"
            "0100"->handName="플러쉬"
            "0110"->handName="플러쉬"
        }
        //원페어,투페어,트리플,포카드,풀 하우스 찾기
        //temp는 처음 카드의 숫자
        temp=c[0]/4
        for(i in 1..4){
            if(temp==c[i]/4) {//같은 숫자이면 count 증가
                count++
            }else{// 다른 숫자를 만났을 경우
                if(count!=1) {//count가 1일 경우는 같은 숫자가 없는 경우이므로 제외
                    tempArray.push(count)
                }
                count=1//count 초기화
            }
            temp=c[i]/4//이전 데이터 값 저장
        }
        // 끝 2개의 카드가 같은 경우는 위의 for 문에서 stack에 저장되지 않으므로 저장해줌
        if(count!=1) {
            tempArray.push(count)
        }
        //스택에 저장되어있는 정보대로 족보 계산
        //null=아무 족보 없음
        //{2},{3},{4}=순서대로 원페어,트리플,포카드
        //{2,2}=투페어
        //{2,3},{3,2}=풀 하우스
        if(tempArray.size==1) {
            when (tempArray.pop()) {
                2 -> handName = "원페어"
                3 -> handName = "트리플"
                4 -> handName = "포카드"
            }
        }else if(tempArray.size==2){
            if(tempArray.search(3)==-1){
                handName="투페어"
            }else{
                handName="풀 하우스"
            }
        }
        return handName
    }
}