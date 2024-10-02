import wii.all.{*, given}

import scala.annotation.tailrec
import scala.scalanative.runtime.{Intrinsics, fromRawPtr}
import scala.scalanative.unsafe.Ptr
import scala.scalanative.unsigned.*

object SnWii {

  final inline val SYS_BASE_CACHED =
    0x80000000

  final inline val SYS_BASE_UNCACHED =
    0xC0000000

  inline def MEM_K0_TO_K1[A](x: Ptr[A]): Ptr[A] =
    fromRawPtr(Intrinsics.castIntToRawPtr(x.toInt + (SYS_BASE_UNCACHED - SYS_BASE_CACHED)))

  final inline val VI_DISPLAY_PIX_SZ = 2

  final inline val VI_NON_INTERLACE = 1

  def main(args: Array[String]): Unit = {
    VIDEO_Init()
    WPAD_Init()

    val rmode = VIDEO_GetPreferredMode(null)
    val xfb = MEM_K0_TO_K1(SYS_AllocateFramebuffer(rmode))

    CON_Init(xfb, 20, 20, (!rmode).fbWidth.toInt, (!rmode).xfbHeight.toInt, (!rmode).fbWidth.toInt * VI_DISPLAY_PIX_SZ)

    VIDEO_Configure(rmode)
    VIDEO_SetNextFramebuffer(xfb)
    VIDEO_SetBlack(false)
    VIDEO_Flush()

    VIDEO_WaitVSync()
    if(((!rmode).viTVMode.toInt & VI_NON_INTERLACE) != 0) VIDEO_WaitVSync()

    CON_InitEx(rmode, 20, 20, (!rmode).fbWidth.toInt - 20, (!rmode).xfbHeight.toInt - 20)

    println("Hello, world!")
    println("From Scala Native!")

    vsyncUntilInput()
  }

  final inline val WPAD_BUTTON_HOME = 0x0080

  @tailrec def vsyncUntilInput(): Unit = {
    WPAD_ScanPads()

    val pressed = WPAD_ButtonsDown(0)

    if((pressed.toInt & WPAD_BUTTON_HOME) == 0) {
      VIDEO_WaitVSync()
      vsyncUntilInput()
    }
  }

}
