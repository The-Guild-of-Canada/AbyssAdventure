package com.abyad.sprite;

import java.util.LinkedHashMap;

import com.abyad.actor.tile.AbstractTile;
import com.abyad.game.Player;
import com.abyad.magic.AbstractMagic;
import com.abyad.utils.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class AbstractSpriteSheet {
	protected LinkedHashMap<String, TextureRegion> sprites;			//A map of sprite regions linked to a string
	
	public static LinkedHashMap<String, AbstractSpriteSheet> spriteSheets;
	
	public static void initializeSpriteSheets() {
		spriteSheets = new LinkedHashMap<String, AbstractSpriteSheet>();
		spriteSheets.put("ZOMBIE", new EntitySprite(Assets.manager.get(Assets.zombie)));
		
		for (String name : Player.characterNames) {
			spriteSheets.put(name, new PlayerSprite(Assets.manager.get(Assets.characterAssets.get(name).get("SPRITE")),
					Assets.manager.get(Assets.characterAssets.get(name).get("ICON"))));
		}
		
		spriteSheets.put("WIND_SLASH", new ProjectileSprite(Assets.manager.get(Assets.windSlash), 4, 1, 6));
		spriteSheets.put("MAGIC_BOLT_PROJECTILE", new ProjectileSprite(Assets.manager.get(Assets.magicBoltProjectile), 1, 2, 6, true));
		spriteSheets.put("FLAME_WHEEL_PROJECTILE", new ProjectileSprite(Assets.manager.get(Assets.flameWheelProjectile), 1, 2, 4, true));
		spriteSheets.put("HEALING_FIELD_PROJECTILE", new ProjectileSprite(Assets.manager.get(Assets.healingFieldProjectile), 1, 2, 6, true));
		spriteSheets.put("SLOW_FIELD_PROJECTILE", new ProjectileSprite(Assets.manager.get(Assets.slowFieldProjectile), 1, 2, 6, true));
		spriteSheets.put("ROCK_SCATTER_PROJECTILE", new ProjectileSprite(Assets.manager.get(Assets.rockProjectile), 1, 1, 6, true));
		spriteSheets.put("DARK_SPIRAL_PROJECTILE", new ProjectileSprite(Assets.manager.get(Assets.darkSpiralProjectile), 1, 1, 6, true));
		
		String[][] chestNames = { {"NORMAL_CLOSED", "NORMAL_OPEN"}, {"RARE_CLOSED", "RARE_OPEN"} };
		spriteSheets.put("CHEST", new BasicSprite(Assets.manager.get(Assets.treasureChest), chestNames));
		
		String[][] statueNames = { {"GODDESS_STATUE"} };
		spriteSheets.put("STATUE", new BasicSprite(Assets.manager.get(Assets.goddessStatue), statueNames));
		
		String[][] houseNames = { {"CLOSED", "OPEN", "ROOF"} };
		spriteSheets.put("HOUSE", new BasicSprite(Assets.manager.get(Assets.house), houseNames));
		
		String[][] blacksmithNames = { {"CLOSED", "OPEN"} };
		spriteSheets.put("BLACKSMITH", new BasicSprite(Assets.manager.get(Assets.blacksmith), blacksmithNames));
		
		String[][] magicShopNames = { {"CLOSED", "OPEN"} };
		spriteSheets.put("MAGIC_SHOP", new BasicSprite(Assets.manager.get(Assets.magicShop), magicShopNames));
		
		String[][] buttonNames = { {"A", "B"}, {"X", "Y"} };
		spriteSheets.put("UI_BUTTONS", new BasicSprite(Assets.manager.get(Assets.buttons), buttonNames));
		String[][] selectionArrowNames = { {"LEFT", "RIGHT"} };
		spriteSheets.put("SELECTION_ARROWS", new BasicSprite(Assets.manager.get(Assets.selectionArrows), selectionArrowNames));
		spriteSheets.put("MAGIC_SELECTION", new BasicSprite(Assets.manager.get(Assets.magicSelectCursor), new String[][] {{"SELECTION"}}));
		spriteSheets.put("HEALTH_CELL", new BarSprite(Assets.manager.get(Assets.healthCell), 5, 11));
		spriteSheets.put("MANA_CELL", new BarSprite(Assets.manager.get(Assets.manaCell), 5, 11));
		
		String[][] weaponIconNames = { {"SWORD", "STAFF", "SPEAR"} };
		spriteSheets.put("WEAPON_ICONS", new BasicSprite(Assets.manager.get(Assets.weaponIcons), weaponIconNames));
		
		String[][] relicNames = { 	{"TON_WEIGHT", "PANIC_CHARM", "VAMPIRIC_FANG", "MANA_SHIELD"},
									{"GREEN_CLOAK", "POCKETWATCH", "RINA'S_SCARF", "LIFETAP"},
									{"LOCKPICK", "LIFE_RING", "SAVINGS_WALLET", "POWER_MAGNET"},
									{"GHOST_SHIELD", "RUBY_PENDANT", "SAPPHIRE_PENDANT", "BLACK_BELT"},
									{"POWER_GLOVE", "ARCANE_BATTERY", "MASKING_PERFUME", "THIEF'S_KNIFE"}};
		spriteSheets.put("RELICS", new BasicSprite(Assets.manager.get(Assets.relics), relicNames));
		
		String[][] inventoryItemNames = { 	{"PUDDING", "DELUXE_PUDDING", "ULTIMATE_PUDDING"} };
		spriteSheets.put("INVENTORY_ITEMS", new BasicSprite(Assets.manager.get(Assets.inventoryItems), inventoryItemNames));
		
		String[][] pickupsNames = { {"HEART", "GOLD", "MANA"} };
		spriteSheets.put("PICKUPS", new BasicSprite(Assets.manager.get(Assets.pickups), pickupsNames));
		
		String[][] carryingNames = { {"KEY"} };
		spriteSheets.put("CARRY", new BasicSprite(Assets.manager.get(Assets.carrying), carryingNames));
		
		String[][] capsuleNames = { {"LIFE_CAPSULE", "MANA_CAPSULE"} };
		spriteSheets.put("CAPSULES", new BasicSprite(Assets.manager.get(Assets.capsules), capsuleNames));
		
		spriteSheets.put("SCROLL", new BasicSprite(Assets.manager.get(Assets.scroll),  new String[][]{{ "SCROLL"}}));
		
		String[][] particleNames = { {"RED", "ORANGE", "YELLOW", "GREEN", "WHITE"}, {"CYAN", "BLUE", "PURPLE", "PINK", "BLACK"} };
		spriteSheets.put("PARTICLES",  new BasicSprite(Assets.manager.get(Assets.particles), particleNames));
		
		String[][] statusArrowNames = { {"DOWN", "UP"} };
		spriteSheets.put("STATUS_ARROW",  new BasicSprite(Assets.manager.get(Assets.statusArrows), statusArrowNames));
		
		String[][] timeMarkNames = { {"EMPTY", "FILLED"} };
		spriteSheets.put("CURSOR_TIME_MARKS", new BasicSprite(Assets.manager.get(Assets.cursorTimeMarks), timeMarkNames));
		
		
		//Magic
		for (String magicName : Assets.magicAssets.keySet()) {
			AbstractMagic magic = AbstractMagic.magicList.get(magicName);
			spriteSheets.put(magicName, new MagicSprite(Assets.magicAssets.get(magicName),
					magic.magicCircleFrames(), magic.magicCircleFrameTime(), magic.particleFrames(), magic.particleFrameTime()));
		}
		
		//Dungeon
		for (String dungeonName : Assets.tileAssets.keySet()) {
			boolean first = true;
			for (String imageKey : Assets.tileAssets.get(dungeonName).keySet()) {
				Texture image = Assets.manager.get(Assets.tileAssets.get(dungeonName).get(imageKey));
				if (first) {
					spriteSheets.put(dungeonName, new BasicSprite(image, image.getHeight() / AbstractTile.TILE_SIZE,
							image.getWidth() / AbstractTile.TILE_SIZE, imageKey));
					first = false;
				}
				else {
					((BasicSprite)spriteSheets.get(dungeonName)).addFullSheet(image, image.getHeight() / AbstractTile.TILE_SIZE,
							image.getWidth() / AbstractTile.TILE_SIZE, imageKey);
				}
			}
		}
		
		for (String imageKey : Assets.townTiles.keySet()) {
			Texture image = Assets.manager.get(Assets.townTiles.get(imageKey));
			if (!spriteSheets.containsKey("TOWN")) {
				spriteSheets.put("TOWN", new BasicSprite(image, image.getHeight() / AbstractTile.TILE_SIZE,
						image.getWidth() / AbstractTile.TILE_SIZE, imageKey));
			}
			else {
				((BasicSprite)spriteSheets.get("TOWN")).addFullSheet(image, image.getHeight() / AbstractTile.TILE_SIZE,
						image.getWidth() / AbstractTile.TILE_SIZE, imageKey);
			}
		}
	}
	
	public AbstractSpriteSheet() {
		sprites = new LinkedHashMap<String, TextureRegion>();		//Initialize the hashmap
	}
	
	public TextureRegion getSprite(String name) {
		return sprites.get(name);									//Get the specified texture
	}
}
