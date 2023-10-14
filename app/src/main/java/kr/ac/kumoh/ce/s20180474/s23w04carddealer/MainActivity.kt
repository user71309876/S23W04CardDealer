package kr.ac.kumoh.ce.s20180474.s23w04carddealer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.ac.kumoh.ce.s20180474.s23w04carddealer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var main: ActivityMainBinding
    private lateinit var model: CardViewModel
    private lateinit var card: Array<ImageView?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        main = ActivityMainBinding.inflate(layoutInflater)
        setContentView(main.root)
        model = ViewModelProvider(this)[CardViewModel::class.java]
        card = arrayOf(main.card1, main.card2, main.card3, main.card4, main.card5)
        model.cards.observe(this, Observer {
            card.forEachIndexed { index, imageView ->
                imageView?.setImageResource(
                    resources.getIdentifier(
                        getCardName(it[index]),
                        "drawable",
                        packageName
                    )
                )
            }
        })

        main.btnShuffle.setOnClickListener {
            model.shuffle()
        }

        //TODO: 하드코딩 없애기
        //시험은 빈킨채워넣기
    }

    private fun getCardName(c: Int): String {
        var shape = when (c / 13) {
            0 -> "spades"
            1 -> "diamonds"
            2 -> "hearts"
            3 -> "clubs"
            else -> "error"
        }
        val number = when (c % 13) {
            -1 -> "joker"
            0 -> "ace"
            in 1..9 -> (c % 13 + 1).toString()
            10 -> "jack"
            11 -> "queen"
            12 -> "king"
            else -> "error"
        }
        if (number in "joker")
            return "c_black_joker"
        if (number in arrayOf("jack", "queen", "king"))
            shape = "${shape}2"
        return "c_${number}_of_${shape}"
    }
}