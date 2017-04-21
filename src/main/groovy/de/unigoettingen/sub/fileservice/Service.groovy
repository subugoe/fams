package de.unigoettingen.sub.fileservice

import io.vertx.core.Launcher

class Service extends Launcher {
    static void main(String[] args) {
        Server.start()
    }
}
