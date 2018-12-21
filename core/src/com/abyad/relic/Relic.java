package com.abyad.relic;

import com.abyad.actor.entity.AbstractEntity;
import com.abyad.actor.entity.PlayerCharacter;
import com.abyad.data.HitEvent;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Passive items, abstract class for now
 *
 */
public abstract class Relic {

	private float activationRate;
	private int cooldown;
	private TextureRegion tex;
	
	public Relic(float activationRate, TextureRegion tex) {
		this.activationRate = activationRate;
		this.tex = tex;
		cooldown = 0;
	}
	
	public float getActivationRate() {
		return activationRate;
	}
	
	public void goOnCooldown() {
		cooldown = 0;
	}
	
	public boolean isOnCooldown() {
		return cooldown > 0;
	}
	
	public void update() {
		if (cooldown > 0) cooldown--;
	}
	
	public void onPassive(PlayerCharacter player) {
		//Do nothing
	}
	
	public void onAttack(PlayerCharacter player) {
		//Do nothing
	}
	
	public void onHit(PlayerCharacter player, HitEvent attack, AbstractEntity hit) {
		//Do nothing
	}
	
	public void onDefense(PlayerCharacter player, HitEvent defense) {
		//Do nothing
	}
}