package de.unigoettingen.sub.fileservice

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

/**
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class FileSystemVerticle extends AbstractVerticle {

    def id

    public FileSystemVerticle(id) {
        this.id = id
    }

    def map = [
            'foo'    : 'bar',
            'sheytan': 'bumidi',
            'bumidi': id
    ]


    void start() {
        super.start();

        def vertx = Vertx.vertx()
        def json = new JsonObject(map)

        for (foo in map) {
            // Deploy the verticle with a configuration.
            vertx.deployVerticle(ConverterVerticle.class.getName(), new DeploymentOptions().setConfig(json));
        }
    }
}
