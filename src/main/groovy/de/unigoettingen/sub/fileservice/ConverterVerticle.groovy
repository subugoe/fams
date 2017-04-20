package de.unigoettingen.sub.fileservice

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject

/**
 *
 * @author Ingo Pfennigstorf <i.pfennigstorf@gmail.com>
 */
class ConverterVerticle extends AbstractVerticle {
    long period = 300L

    int counter = 0

    @Override
    public void start() {

        // Every `period` ms, the given Handler is called.
        vertx.setPeriodic(period, { l ->
            compute(config().getString('bumidi'))
            send()
        })
    }

    void compute(String py) {
        println(py + counter)
        counter ++
    }

    private void send() {
      vertx.eventBus().publish('Ahoi', toJson());
    }

    private JsonObject toJson() {
      return new JsonObject()
          .put('exchange', 'vert.x stock exchange')
          .put('symbol', counter)
    }
}
