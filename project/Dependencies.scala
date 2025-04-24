import sbt.*

object Dependencies {

  val logback = "ch.qos.logback" %  "logback-classic"     % "1.5.18"
  val blaze   = "org.http4s"     %% "http4s-blaze-server" % "0.23.17"

  val singles: Seq[ModuleID] = Seq(logback, blaze)

  trait ModuleBatch {
    val modules: Seq[ModuleID]
  }

  object Cats extends ModuleBatch {
    val modules: Seq[ModuleID] = Seq(
      "org.typelevel" %% "cats-core"   % "2.13.0",
      "org.typelevel" %% "cats-effect" % "3.6.1"
    )
  }

  object Tapir extends ModuleBatch {
    val version: String = "1.11.24"
    val modules: Seq[ModuleID] = Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-core"              % version,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"     % version,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % version,
      "com.softwaremill.sttp.tapir" %% "tapir-json-tethys"       % version
    )
  }

  object Tethys extends ModuleBatch {
    val version: String = "0.29.4"
    val modules: Seq[ModuleID] = Seq(
      "com.tethys-json" %% "tethys-core"       % version,
      "com.tethys-json" %% "tethys-jackson213" % version,
      "com.tethys-json" %% "tethys-derivation" % version
    )
  }

  object Pureconfig extends ModuleBatch {
    val version: String = "0.17.8"
    val modules: Seq[ModuleID] = Seq(
      "com.github.pureconfig" %% "pureconfig"             % version,
      "com.github.pureconfig" %% "pureconfig-core"        % version,
      "com.github.pureconfig" %% "pureconfig-generic"     % version,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % version
    )
  }

  object Tofu extends ModuleBatch {
    val version: String = "0.13.7"
    val modules: Seq[ModuleID] = Seq(
      "tf.tofu" %% "tofu-kernel"             % version,
      "tf.tofu" %% "tofu-core-ce3"           % version,
      "tf.tofu" %% "tofu-logging"            % version,
      "tf.tofu" %% "tofu-logging-derivation" % version
    )
  }

  object Derevo extends ModuleBatch {
    val version: String = "0.11.6"
    val modules: Seq[ModuleID] = Seq(
      "org.manatki" %% "derevo-core"       % version,
      "org.manatki" %% "derevo-cats"       % version,
      "org.manatki" %% "derevo-pureconfig" % version,
      "org.manatki" %% "derevo-tethys"     % version
    )
  }

  object FS2 extends ModuleBatch {
    val version: String = "3.12.0"
    val modules: Seq[ModuleID] = Seq(
      "co.fs2" %% "fs2-core" % version,
      "co.fs2" %% "fs2-io"   % version
    )
  }

  object Testing extends ModuleBatch {
    val modules: Seq[ModuleID] = Seq(
      "org.scalatest" %% "scalatest"                     % "3.2.19",
      "org.typelevel" %% "cats-effect-testing-scalatest" % "1.6.0"
    ).map(_ % Test)
  }

  val batches: Seq[ModuleBatch] = Seq(
    Cats,
    Tapir,
    Tethys,
    Pureconfig,
    Tofu,
    Derevo,
    FS2,
    Testing
  )

}
