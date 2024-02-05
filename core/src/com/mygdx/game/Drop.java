package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Drop extends Game {
	public static SpriteBatch batch;
	public BitmapFont font;
	float fontSize = 2.0F;
	public BitmapFont font_cont_pts;
	public BitmapFont font_cont_vidas;
	public BitmapFont font_a;

	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont(); // use libGDX's default Arial font

		//vamos a crear el contador con estas variables
		font_cont_pts = new BitmapFont();
		font_cont_pts.setColor(Color.WHITE);
		font_cont_pts.getData().setScale(fontSize);

		font_cont_vidas = new BitmapFont();
		font_cont_vidas.setColor(Color.RED);
		font_cont_vidas.getData().setScale(fontSize);


		this.setScreen(new MainMenuScreen(this));


	}

	public void render() {

		super.render(); // important!
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
		font_cont_vidas.dispose();
		font_cont_pts.dispose();

	}

}
