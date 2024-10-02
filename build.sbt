import bindgen.interface.Binding
import bindgen.interface.Includes

import scala.scalanative.build.GC

val dkpGcc = "13.1.0"
val devKitPro = file(sys.env("DEVKITPRO"))
val devKitPpc = file(sys.env("DEVKITPPC"))

lazy val lib = (project in file("lib"))
  .enablePlugins(ScalaNativePlugin, BindgenPlugin)
  .settings(
    organization := "tf.bug",
    name := "sn-wii-lib",
    version := "0.1.0",
    scalaVersion := "3.5.1",
    bindgenBindings := Seq(
      Binding(
        (Compile / resourceDirectory).value / "scala-native" / "wii.h",
        "wii"
      ).withSystemIncludes(Includes.None)
        .addCImport("wii.h")
        .addBindgenArguments(List(
          "--clang", "--target=ppc32-none-eabi",
          "--clang", "-DGEKKO", "--clang", "-mcpu=750",
          "--clang", "-D__wii__", "--clang", "-DHW_RVL",
          "--clang", "-ffunction-sections", "--clang", "-fdata-sections",
          "--clang", "-nostdlibinc",
          "--clang", "-isystem", "--clang", (devKitPpc / "powerpc-eabi" / "include").toString,
          "--clang", "-cxx-isystem", "--clang", (devKitPpc / "powerpc-eabi" / "include" / "c++" / dkpGcc).toString,
          "--clang", "-cxx-isystem", "--clang", (devKitPpc / "powerpc-eabi" / "include" / "c++" / dkpGcc / "powerpc-eabi").toString,
          "--clang", "-isystem", "--clang", (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "include").toString,
          "--clang", "-isystem", "--clang", (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "include-fixed").toString,
          "--clang", "-isystem", "--clang", (devKitPro / "libogc" / "include").toString,
        )),
    ),
    nativeConfig ~= { c =>
      c.withTargetTriple("ppc32-none-eabi")
        .withCompileOptions(_ ++ Seq(
          "-DGEKKO", "-mcpu=750",
          "-D__wii__", "-DHW_RVL", "-ffunction-sections", "-fdata-sections",
          "-nostdinc", "-nostdinc++",
          "-isystem", (devKitPpc / "powerpc-eabi" / "include").toString,
          "-cxx-isystem", (devKitPpc / "powerpc-eabi" / "include" / "c++" / dkpGcc).toString,
          "-cxx-isystem", (devKitPpc / "powerpc-eabi" / "include" / "c++" / dkpGcc / "powerpc-eabi").toString,
          "-isystem", (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "include").toString,
          "-isystem", (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "include-fixed").toString,
          "-isystem", (devKitPro / "libogc" / "include").toString,
        ))
        .withLinkingOptions(_ ++ Seq(
          "-DGEKKO", "-mcpu=750",
          "-D__wii__", "-DHW_RVL", "-ffunction-sections", "-fdata-sections",
          "-Wl,--gc-sections", "-nostartfiles",
          (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "crtend.o").toString,
          (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "ecrtn.o").toString,
          (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "ecrti.o").toString,
          (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "crtbegin.o").toString,
          (devKitPpc / "powerpc-eabi" / "lib" / "crtmain.o").toString,
          s"-L${devKitPpc / "powerpc-eabi" / "lib"}",
          "-Wl,--start-group", "-lsysbase", "-lc", "-Wl,--end-group",
          "-Trvl.ld"
        ))
        .withGC(GC.boehm)
        .withMultithreading(false)
    }
  )

lazy val main = (project in file("main"))
  .enablePlugins(ScalaNativePlugin)
  .settings(
    organization := "tf.bug",
    name := "sn-wii-lib",
    version := "0.1.0",
    scalaVersion := "3.5.1",
    scalacOptions ++= Seq(
      "-no-indent", "-old-syntax"
    ),
    mainClass := Some("SnWii"),
    nativeConfig ~= { c =>
      c.withTargetTriple("ppc32-none-eabi")
        .withCompileOptions(_ ++ Seq(
          "-DGEKKO", "-mcpu=750",
          "-D__wii__", "-DHW_RVL", "-ffunction-sections", "-fdata-sections",
          "-nostdinc", "-nostdinc++",
          "-isystem", (devKitPpc / "powerpc-eabi" / "include").toString,
          "-cxx-isystem", (devKitPpc / "powerpc-eabi" / "include" / "c++" / dkpGcc).toString,
          "-cxx-isystem", (devKitPpc / "powerpc-eabi" / "include" / "c++" / dkpGcc / "powerpc-eabi").toString,
          "-isystem", (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "include").toString,
          "-isystem", (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "include-fixed").toString,
          "-isystem", (devKitPro / "libogc" / "include").toString,
        ))
        .withLinkingOptions(_ ++ Seq(
          "-DGEKKO", "-mcpu=750",
          "-D__wii__", "-DHW_RVL", "-ffunction-sections", "-fdata-sections",
          "-Wl,--gc-sections", "-nostartfiles",
          (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "crtend.o").toString,
          (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "ecrtn.o").toString,
          (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "ecrti.o").toString,
          (devKitPpc / "lib" / "gcc" / "powerpc-eabi" / dkpGcc / "crtbegin.o").toString,
          (devKitPpc / "powerpc-eabi" / "lib" / "crtmain.o").toString,
          s"-L${devKitPpc / "powerpc-eabi" / "lib"}",
          "-Wl,--start-group", "-lsysbase", "-lc", "-Wl,--end-group",
          "-Trvl.ld"
        ))
        .withGC(GC.boehm)
        .withMultithreading(false)
    }
  )
  .dependsOn(lib)
