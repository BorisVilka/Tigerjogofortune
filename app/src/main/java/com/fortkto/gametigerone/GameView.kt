package com.fortkto.gametigerone


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.lang.Exception
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class GameView(ctx: Context, attributeSet: AttributeSet): SurfaceView(ctx,attributeSet) {


    var bg = BitmapFactory.decodeResource(ctx.resources,R.drawable.bg1)
    var avia = BitmapFactory.decodeResource(ctx.resources,R.drawable.tiger)
    var tube = BitmapFactory.decodeResource(ctx.resources,R.drawable.part)
    var bomb = BitmapFactory.decodeResource(ctx.resources,R.drawable.bomb)

    public var score = 0
    private val random = Random()
    var millis = 0
    private var listener: EndListener? = null
    private var paintB: Paint = Paint(Paint.DITHER_FLAG)

    private var bx = 0f
    private var by = 0f

    var best = ctx.getSharedPreferences("prefs",Context.MODE_PRIVATE).getInt("best",0)

    var paintT = Paint().apply {
        color = Color.BLACK
        textSize = 60f
    }
    var paintT1= Paint().apply {
        color = Color.BLACK
        textSize = 100f
    }

    var cords = mutableListOf<Float>()

    init {
        tube = Bitmap.createScaledBitmap(tube, (tube.width*0.4).toInt(),
            (tube.height*0.4).toInt(),true)
        bomb = Bitmap.createScaledBitmap(bomb,bomb.width/8,bomb.height/8,true)
        avia = Bitmap.createScaledBitmap(avia,avia.width/10,avia.height/10,true)
         holder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                val canvas = holder.lockCanvas()
                if(canvas!=null) {
                    bg = Bitmap.createScaledBitmap(bg,canvas.width,canvas.height,true)
                    draw(canvas)
                    deltaX = canvas.width/40f
                    bx = 50+tube.width/2f-avia.width/2f
                    by = canvas.height-tube.height.toFloat()-avia.height/2f
                    cords.add(50f)
                    width1 = canvas.width.toFloat()
                    cords.add(canvas.width/2f-tube.width/2f)
                    cords.add(canvas.width-tube.width-50f)
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                }

        })
        val updateThread = Thread {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    if (!paused) {
                        update.run()
                        millis ++
                    }
                }
            }, 0, 26)
        }

        updateThread.start()
    }
    var width1 = 0f
    var click = 0
    var move = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                click++
                if(!move) {
                    SoundsManager.getInstance().startClickSound()
                    if(event.x>width1/2) {
                        if(ind<2) {
                            napr = 1
                            ind++
                        }
                    } else if(ind>0) {
                        ind--
                        napr = -1
                    }
                    move = true
                    delta = -10
                }
            }
        }
        return true
    }

    var delta = 0
    var deltaX = 0f
    var paused = false
    val list = mutableListOf<Model>()
    var ind = 0
    var napr = 1

    val update = Runnable{
        if(paused) return@Runnable
        var isEnd = false
        try {
            val canvas = holder.lockCanvas()
            if(click>0) {
                if(move) {
                    delta++
                    by += delta
                    bx += napr*deltaX
                    if(abs(bx-cords[ind])<=deltaX) {
                        move = false
                        delta = 0
                        by = canvas.height-tube.height.toFloat()-avia.height/2f
                        bx = cords[ind]-avia.width/2f+tube.width/2f
                    }
                }
                var add = 1
                while(list.size<3) {
                    val tmp = random.nextInt(cords.size)
                    var min1 = 0f
                   // for(i in list) min1 = min(min1,i.y)
                    list.add(Model(cords[tmp]+tube.width/2f-bomb.width/2f,min1-canvas.height/3f*add))
                    add++
                }
                var i = 0
                while(i<list.size) {
                    val j = list[i]
                    j.y += 7
                    if(
                        (bx>=j.x && bx<=j.x+bomb.width || bx<j.x && j.x<bx+avia.width)
                        && (by>=j.y && by<=j.y+bomb.height || j.y>by && j.y<by+bomb.height)
                    ) {
                        isEnd = true
                        list.removeAt(i)
                        break
                    } else if(j.y>=canvas.height-tube.height+bomb.height) {
                        score++
                        if(score>best) best = score
                        list.removeAt(i)
                    } else i++
                }
            }
            canvas.drawBitmap(bg,0f,0f,paintB)
            for(i in cords) canvas.drawBitmap(tube,i,canvas.height-tube.height.toFloat(),paintB)
            canvas.drawBitmap(avia,bx,by,paintB)
            for(i in list) canvas.drawBitmap(bomb,i.x,i.y,paintB)
            val cur = "Score: $score"
            val best = "Best: $best"
            canvas.drawText(cur,canvas.width/2f-paintT.measureText(cur)/2f,80f,paintT)
            canvas.drawText(best,canvas.width/2f-paintT.measureText(best)/2f,150f,paintT)
            if(click==0) {
                val tap = "TAP TO\nJUMP"
                canvas.drawText(tap,canvas.width/2f-paintT1.measureText(tap)/2f,canvas.height/3f,paintT1)
            }
            holder.unlockCanvasAndPost(canvas)
            // Log.d("TAG","$isEnd")
            if(isEnd) {
                Log.d("TAG","END")
                paused = true
                if(listener!=null) listener!!.end()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setEndListener(list: EndListener) {
        this.listener = list
    }
    fun togglePause() {
        paused = !paused
    }
    companion object {
        interface EndListener {
            fun end();
        }
        data class Model(var x:Float, var y:Float)
    }

}