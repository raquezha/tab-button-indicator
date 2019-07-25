package sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_sample.*
import net.raquezha.buttonindicator.ButtonIndicator

class MainActivity : AppCompatActivity() {

    private lateinit var labels: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        labels = arrayListOf(
            getString(R.string.tab_hot),
            getString(R.string.tab_cold),
            getString(R.string.tab_warm),
            getString(R.string.tab_frozen)
        )

        indicator.initializeButtons(labels)

        val items = labels
        val adapter = SampleAdapter(this@MainActivity, items)
        vpSample.adapter = adapter

        vpSample.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                indicator.selectView(position)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                indicator.setProgress(position, positionOffset)
            }
        })

        indicator.setListener(object : ButtonIndicator.TransactionIndicatorListener {
            override fun onClickButton(name: String, index: Int) {
                indicator.selectView(index)
                //vpSample.setCurrentItem(index, true)
            }

        })

    }
}
