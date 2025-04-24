package com.app.config

import derevo.derive
import derevo.pureconfig.pureconfigReader

@derive(pureconfigReader)
case class ServerConfig(host: String, port: Int)
