package com.app.config

import derevo.derive
import derevo.pureconfig.pureconfigReader

@derive(pureconfigReader)
case class AppConfig(name: String, version: String, server: ServerConfig)
