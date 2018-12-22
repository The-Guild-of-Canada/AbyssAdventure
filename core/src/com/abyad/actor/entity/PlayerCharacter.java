package com.abyad.actor.entity;

import java.util.ArrayList;

import com.abyad.actor.mapobjects.items.CarryingItem;
import com.abyad.controls.PlayerController;
import com.abyad.data.HitEvent;
import com.abyad.game.Player;
import com.abyad.interfaces.Interactable;
import com.abyad.relic.Relic;
import com.abyad.sprite.AbstractSpriteSheet;
import com.abyad.sprite.EntitySprite;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PlayerCharacter extends HumanoidEntity{

	private static ArrayList<PlayerCharacter> players = new ArrayList<PlayerCharacter>();		//List of all characters
	
	private Player player;			//The player object this character is associated to
	
	private Vector2 knockbackVelocity = new Vector2(0, 0);		//Velocity of knockback
	private int knockbackLength = 0;							//Length of knockback
	
	private boolean attackHeld = false;							//Boolean to check if the attack button is held
	private boolean attacking;									//Boolean to check if the character is in the middle of attacking
	private String weapon;										//The weapon being used (as we might have different weapons)
	
	private ArrayList<Interactable> interactableObjects = new ArrayList<Interactable>();
	private ArrayList<Relic> relics = new ArrayList<Relic>();
	private CarryingItem heldItem;
	
	private final float MAX_SPEED = 2.5f;
	
	public PlayerCharacter(Player player, float x, float y) {
		super(x, y);
		
		this.player = player;
		if (player.getNumber() == 1) {
			sprite = (EntitySprite)AbstractSpriteSheet.spriteSheets.get("GIRL_1");
		}
		else {
			sprite = (EntitySprite)AbstractSpriteSheet.spriteSheets.get("BOY_1");
		}
		weapon = "SWORD";
		updateHitbox();
		
		players.add(this);
	}
	
	/**
	 * Where all the controls of the character lies.
	 */
	@Override
	public void act(float delta) {
		super.act(delta);
		if (!markForRemoval) {
			PlayerController controller = player.getController();
			//The following two lines are used to check the movement of the character. Note that the controller is returning a float
			//The values are getting squared just as a sensitivity thing.
			float xChange = (float)Math.pow(controller.rightPressed(), 2) - (float)Math.pow(controller.leftPressed(), 2);
			float yChange = (float)Math.pow(controller.upPressed(), 2) - (float)Math.pow(controller.downPressed(), 2);
			
			//Activate relic passives
			for (Relic relic : relics) {
				relic.update();
				if (!relic.isOnCooldown() && Math.random() < relic.getActivationRate()) {
					relic.onPassive(this);
				}
			}
			
			//Knockback the player if currently in knockback
			if (knockbackLength > 0) {
				move(knockbackVelocity);
				knockbackLength--;
			}
			else if (!attacking) {
				
				interactableObjects.clear();
				for (Interactable interactable : Interactable.interactables) {
					if (!(interactable instanceof CarryingItem) || !isHoldingItem()) {
						if (isOverlapping(interactable.getInteractBox(), getCollideBox())) {
							interactableObjects.add(interactable);
							interactable.setCanInteract(true);
						}
					}
				}
				
				//If not in an attack animation
				if (controller.attackPressed() && !attackHeld) {
					//This checks to see if the player has pressed the attack button
					//First see if the player can interact with something
					boolean interactedWithSomething = false;
					for (Interactable interactable : interactableObjects) {
						if (interactable.interact(this)) {
							interactedWithSomething = true;
						}
					}
					//If no interactions happen, then try attacking or dropping item
					if (!interactedWithSomething) {
						if (!isHoldingItem()) {
							//If there is nothing to interact and not holding an item, just attack
							attacking = true;
							if (state.equals("IDLE")) {
								//If the player was in idle, give a small forward movement. May remove or lower this.
								velocity.setAngle(((int)(velocity.angle() + 45) / 90) * 90.0f);
								velocity.setLength(1.5f);
							}
							setState("ATTACK - " + weapon);	//Set the state to attacking
						}
						else {
							//Drop item holding
							dropItemOnGround();
						}
					}
				}
				else if (xChange == 0 && yChange == 0) {
					//No movement? the character is idling
					if (!isHoldingItem()) {
						setState("IDLE");
					}
					else {
						setState("CARRYING_IDLE");
					}
				}
				else {
					//This happens if the character is moving, scale the change to match velocity
					if (!isHoldingItem()) {
						velocity.x = xChange * MAX_SPEED;
						velocity.y = yChange * MAX_SPEED;
						if (velocity.len() > MAX_SPEED) velocity.setLength(MAX_SPEED);	//Sometimes the velocity is over the actual max speed, so set it back
						setState("MOVE", velocity.len() / 2.0f);		//Set the state and use partial frames if the character is moving slow
						move(velocity);
					}
					else {
						//If carrying an item, slow by weight, a set correct state
						float slowedSpeed =  MAX_SPEED / (heldItem.getWeight() + 1.0f);
						velocity.x = xChange * slowedSpeed;
						velocity.y = yChange * slowedSpeed;
						if (velocity.len() > slowedSpeed) velocity.setLength(slowedSpeed);
						setState("CARRYING_MOVE", velocity.len() / 2.0f);		//Set the state and use partial frames if the character is moving slow
						move(velocity);
					}
				}
			}
			else {
				//Sets the state into attacking
				setState("ATTACK - " + weapon);
				if (weapon.equals("SWORD")) {
					//This is if the player is using a sword
					velocity.setLength(velocity.len() / 1.08f);
					move(velocity);
					checkCollisions();		//Checks to see if the attack is hitting
					if (framesSinceLast >= 30) attacking = false;		//Ends the attack after a certain amount of frames
				}
			}
			attackHeld = controller.attackPressed();
		}
	}
	
	/**
	 * Used if the player is attacking
	 */
	public void checkCollisions() {
		ArrayList<Rectangle> hurtboxes = getHurtbox();
		ArrayList<AbstractEntity> entities = AbstractEntity.getEntities();
		for (AbstractEntity entity : entities) {
			if (!isSameTeam(entity)) {
				ArrayList<Rectangle> otherHitbox = entity.getHitbox();
				if (isOverlapping(hurtboxes, otherHitbox)) {
					//Attacked someone
					Vector2 knockback = new Vector2(entity.getCenterX() - getCenterX(), entity.getCenterY() - getCenterY());
					knockback.setLength(4.0f);
					int kbLength = 8;
					int damage = 1;
					HitEvent event = new HitEvent(this, entity, damage, knockback, kbLength);
					
					//Activate relic hit effects (modifies the event)
					for (Relic relic : relics) {
						if (!relic.isOnCooldown() && Math.random() < relic.getActivationRate()) {
							relic.onHit(this, event, entity);
						}
					}
					
					entity.takeDamage(event);
				}
			}
		}
	}
	
	public void pickupRelic(Relic relic) {
		relics.add(relic);
	}
	
	public boolean carryItem(CarryingItem carry) {
		if (!isHoldingItem()) {
			heldItem = carry;
			return true;
		}
		else return false;
	}
	
	public void dropItemOnGround() {
		heldItem.setPosition(getCenterX(), getCenterY());
		if (!state.equals("CARRYING_IDLE")) {
			heldItem.getVelocity().set(velocity);
		}
		else {
			heldItem.getVelocity().setLength(0);
		}
		getStage().addActor(heldItem);
		heldItem.spawn();
		heldItem = null;
	}
	
	public CarryingItem getHeldItem() {
		return heldItem;
	}
	
	public boolean isHoldingItem() {
		return heldItem != null;
	}
	
	public void removeHeldItem() {
		heldItem = null;
	}
	
	@Override
	public void draw(Batch batch, float a) {
		super.draw(batch, a);
		if (inView()) {
			if (isHoldingItem()) {
				batch.draw(heldItem.getTexture(), getX() - getOriginX(), getY() + (getOriginY() / 4),
						getOriginX(), getOriginY(), heldItem.getTexture().getRegionWidth(), heldItem.getTexture().getRegionHeight(),
						getScaleX(), getScaleY(), getRotation());
			}
		}
	}
	
	/**
	 * Debug tool to draw hitboxes. VERY VERY unoptimized though (due to the Pixmap object drawing TONS of textures)
	 * @param batch
	 * @param a
	 */
	public void drawHitbox(Batch batch, float a) {
		for (Rectangle hitbox : hitboxes) {
			Pixmap pixmap = new Pixmap((int)hitbox.getWidth(), (int)hitbox.getHeight(), Format.RGBA8888 );
			pixmap.setColor( 1, 0, 0, 0.25f );
			pixmap.fill();
			Texture box = new Texture( pixmap );
			pixmap.dispose();
			
			batch.draw(box, hitbox.getX(), hitbox.getY());
		}
		
		for (Rectangle hurtbox : getHurtbox()) {
			Pixmap pixmap = new Pixmap((int)hurtbox.getWidth(), (int)hurtbox.getHeight(), Format.RGBA8888 );
			pixmap.setColor( 0, 1, 0, 0.25f );
			pixmap.fill();
			Texture box = new Texture( pixmap );
			pixmap.dispose();
			
			batch.draw(box, hurtbox.getX(), hurtbox.getY());
		}
	}
	
	/**
	 * The team, all players are on the same team so they can't hurt each other.
	 */
	@Override
	public String getTeam() {
		return "PLAYERS";
	}
	
	/**
	 * Method overriden to set knockback and invuln period for the player. Also to lower hp
	 */
	@Override
	public void takeDamage(HitEvent event) {
		if (!isInvuln()) {
			
			//Activate relic defense effects (modifies the event)
			for (Relic relic : relics) {
				if (!relic.isOnCooldown() && Math.random() < relic.getActivationRate()) {
					relic.onDefense(this, event);
				}
			}
			if (isHoldingItem()) {
				dropItemOnGround();
			}
			
			knockbackVelocity = event.getKnockbackVelocity();
			knockbackLength = event.getKnockbackLength();
			hp -= event.getDamage();
			invulnLength = 40;
		}
	}
	
	/**
	 * Draws the hurtbox given the player is attacking. It's rather complex.
	 * @return
	 */
	public ArrayList<Rectangle> getHurtbox(){
		ArrayList<Rectangle> hurtboxes = new ArrayList<Rectangle>();
		if (state.equals("ATTACK - SWORD")) {
			int[] attackLengths = {10, 14, 18, 30};		//The frame thresholds for each sprite. Only the second and third frame are attack frames
			int frame = 0;
			int dir = (int)((velocity.angle() + 45) / 90) % 4; //0 - Right, 1 - Back, 2 - Left, 3 - Front
			float xOffset = 0;	//Offsets to set the hurtbox
			float yOffset = 0;	//Offsets to set the hurtbox correctly
			while (frame < 4 && framesSinceLast >= attackLengths[frame]) {
				frame++;	//This figures out what frame the player is in
			}
			if (frame >= 4) frame = 3;
			
			if (frame == 1) {
				//This is the initial side swing
				Rectangle hurtbox;
				//Left - Right direction
				if (dir % 2 == 0) {
					hurtbox = new Rectangle(getCenterX() - 8, getCenterY() - 5, 16, 10);
					xOffset = (1 - dir) * 4.5f;	//Fancy math for offsetting the hurtbox
					yOffset = (dir - 1) * 6;
				}
				//Front - Back direction
				else {
					hurtbox = new Rectangle(getCenterX() - 5, getCenterY() - 8, 10, 16);
					xOffset = (2 - dir) * 6;
					yOffset = (2 - dir) * 4.5f;
				}
				hurtbox.setPosition(hurtbox.getX() + xOffset, hurtbox.getY() + yOffset);
				hurtboxes.add(hurtbox);
			}
			
			if (frame == 2) {
				//This is the front swing
				Rectangle hurtbox;
				//Left - Right direction
				if (dir % 2 == 0) {
					hurtbox = new Rectangle(getCenterX() - 6, getCenterY() - 10, 12, 20);
					xOffset = (1 - dir) * 8.0f;
				}
				//Front - Back direction
				else {
					hurtbox = new Rectangle(getCenterX() - 10, getCenterY() - 6, 20, 12);
					yOffset = (2 - dir) * 8.0f;
				}
				hurtbox.setPosition(hurtbox.getX() + xOffset, hurtbox.getY() + yOffset);
				hurtboxes.add(hurtbox);
			}
			return hurtboxes;
		}
		return hurtboxes;
	}
	
	@Override
	public boolean remove() {
		players.remove(this);		//Removes this player from the array
		return super.remove();
	}
	
	public static ArrayList<PlayerCharacter> getPlayers(){
		return players;
	}

}
