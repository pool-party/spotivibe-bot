package com.github.pool_party.spotivibe

import com.github.pool_party.flume.configuration.AbstractConfiguration

object Configuration : AbstractConfiguration() {

    val CLIENT_ID by string()
    val CLIENT_SECRET by string()
}
