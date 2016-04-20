package game.blocks;

import flounder.devices.*;
import flounder.engine.*;
import flounder.maths.vectors.*;
import flounder.noise.*;
import flounder.physics.*;

import static org.lwjgl.glfw.GLFW.*;

public class Chunk {
	public static final int CHUNK_LENGTH = 32;
	public static final int CHUNK_HEIGHT = 28;
	public static final int DIRT_DEPTH = 3;

	private final Vector2f position;
	private final Block[][][] blocks;
	private final AABB aabb;

	protected Chunk(final Vector2f position, final NoisePerlin perlinNoise) {
		this.position = position;
		this.blocks = new Block[CHUNK_LENGTH][CHUNK_HEIGHT][CHUNK_LENGTH];
		this.aabb = new AABB();
		this.aabb.setMaxExtents(position.x + (CHUNK_LENGTH / 2), (CHUNK_HEIGHT / 2), position.y + (CHUNK_LENGTH / 2));
		this.aabb.setMinExtents(position.x - (CHUNK_LENGTH / 2), (CHUNK_HEIGHT / 2), position.y - (CHUNK_LENGTH / 2));
		generate(perlinNoise);
	}

	private void generate(final NoisePerlin perlinNoise) {
		for (int x = 0; x < CHUNK_LENGTH; x++) {
			for (int z = 0; z < CHUNK_LENGTH; z++) {
				double height = perlinNoise.noise2((position.x + x) / CHUNK_HEIGHT, (position.y + z) / CHUNK_HEIGHT);

				// Negate any negative noise values.
				if (height < 0) {
					height = -height;
				}

				// Multiply by the max height, then round up.
				height = Math.ceil(height * CHUNK_HEIGHT);

				for (int y = (int) height; y >= 0 && y < CHUNK_HEIGHT; y--) {
					int depth = (int) (height - y);

					BlockType type;

					if (depth == 0) {
						type = BlockType.get("game::grass");
					} else if (depth <= DIRT_DEPTH) {
						type = BlockType.get("game::dirt");
					} else {
						type = BlockType.get("game::stone");
					}

					blocks[x][y][z] = new Block(type, new Vector3f(position.x + x + (x * type.getExtent()), y + (y * type.getExtent()), position.y + z + (z * type.getExtent())), ((x+z) % 2) == 0);
				}
			}
		}
	}

	public boolean renderable() {
		return FlounderEngine.getCamera().getViewFrustum().aabbInFrustum(aabb);
	}

	public Block[][][] getBlocks() {
		return blocks;
	}

	public void addBlock(final Block block) {
		this.blocks[Math.round(block.getPosition().x)][Math.round(block.getPosition().y)][Math.round(block.getPosition().z)] = block;
	}

	public void removeBlock(final Block block) {
		this.blocks[Math.round(block.getPosition().x)][Math.round(block.getPosition().y)][Math.round(block.getPosition().z)] = null;
	}

	public void update() {
	//	for (int x = 0; x < CHUNK_LENGTH; x++) {
	//		for (int y = 0; y < CHUNK_HEIGHT; y++) {
	//			for (int z = 0; z < CHUNK_LENGTH; z++) {
	//				if (blocks[x][y][z] != null) {
	//					blocks[x][y][z].update(!ManagerDevices.getKeyboard().getKey(GLFW_KEY_Y));
	//				}
	//			}
	//		}
	//	}

		// TODO: Sort blocks back to front.
	}
}