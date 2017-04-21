package de.unigoettingen.sub.fams

import io.vertx.core.Launcher

class Service extends Launcher {
    static void main(String[] args) {
        Server.start()
    }
}
