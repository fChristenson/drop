package com.badlogic.drop;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Drop implements ApplicationListener {
    Texture dropImage;
    Texture bucketImage;
    Music music;
    SpriteBatch batch;
    OrthographicCamera camera;
    Rectangle bucket;
    Vector3 touchPos = new Vector3();
    Array<Rectangle> raindrops;
    long lastDrop;

    @Override
    public void create() {
        raindrops = new Array<Rectangle>();
        spawnRainddrop();

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        music.setLooping(true);
        music.play();
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        music.dispose();
        bucketImage.dispose();
        batch.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bucketImage, bucket.x, bucket.y);

        for (Rectangle drop : raindrops)
            batch.draw(dropImage, drop.x, drop.y);

        batch.end();

        if (TimeUtils.nanoTime() - lastDrop > 1000000000)
            spawnRainddrop();

        Iterator<Rectangle> iterator = raindrops.iterator();
        while (iterator.hasNext()) {
            Rectangle next = iterator.next();
            next.y -= 200 * Gdx.graphics.getDeltaTime();

            if (next.y + 64 < 0)
                iterator.remove();

            else if (next.overlaps(bucket)) {
                iterator.remove();
            }
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;

        } else if (Gdx.input.isKeyPressed(Keys.A)) {
            bucket.x -= 200 * Gdx.graphics.getDeltaTime();

        } else if (Gdx.input.isKeyPressed(Keys.D)) {
            bucket.x += 200 * Gdx.graphics.getDeltaTime();

        }

        if (bucket.x < 0)
            bucket.x = 0;

        if (bucket.x > 800 - 64)
            bucket.x = 800 - 64;
    }

    private void spawnRainddrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDrop = TimeUtils.nanoTime();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
