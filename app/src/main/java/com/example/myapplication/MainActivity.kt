package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

	private lateinit var edtA: AppCompatEditText
	private lateinit var edtB: AppCompatEditText
	private lateinit var edtD: AppCompatEditText
	private lateinit var edtG: AppCompatEditText

	private lateinit var txtC: AppCompatTextView
	private lateinit var txtE: AppCompatTextView
	private lateinit var txtF: AppCompatTextView
	private lateinit var btnSubmit: AppCompatButton

	private lateinit var rootLayout: NestedScrollView

	private fun calculate(a: Double, b: Double, d: Double, g: Double) {
		val c = a.times(b.div(100))
		val e = c.div(d)
		val f = e.div(g)

		("risk of money : " + c.convertNumbersOnly(digits = 8)).also { txtC.text = it }
		("Pip value : " + e.convertNumbersOnly(digits = 8)).also { txtE.text = it }
		("lot : " + f.convertNumbersOnly(digits = 8)).also { txtF.text = it }
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		edtA = findViewById(R.id.edt_a)
		edtB = findViewById(R.id.edt_b)
		edtD = findViewById(R.id.edt_d)
		edtG = findViewById(R.id.edt_g)
		txtC = findViewById(R.id.txt_c)
		txtE = findViewById(R.id.txt_e)
		txtF = findViewById(R.id.txt_f)
		btnSubmit = findViewById(R.id.btn_submit)
		rootLayout = findViewById(R.id.root_layout)
		btnSubmit.setOnClickListener {
			when {
				edtA.isEmpty or edtB.isEmpty or edtD.isEmpty or edtG.isEmpty -> showSnackBar("fill the fields first")
				else -> {
					calculate(edtA.textDouble, edtB.textDouble, edtD.textDouble, edtG.textDouble)
					rootLayout.apply { scrollTo(0, bottom) }
				}
			}
		}
	}

	private fun showSnackBar(text: String) {
		Snackbar.make(rootLayout, text, Snackbar.LENGTH_SHORT).show()
	}

}