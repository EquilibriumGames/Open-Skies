package ebon.entities.components;

import ebon.entities.*;
import ebon.entities.loading.*;
import flounder.animation.*;
import flounder.collada.*;
import flounder.collada.geometry.*;
import flounder.collada.joints.*;
import flounder.helpers.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;

import java.util.*;

/**
 * Creates a animation used to set animation properties.
 */
public class ComponentAnimation extends IEntityComponent {
	public static final int ID = EntityIDAssigner.getId();

	private ModelAnimated modelAnimated;
	private float scale;

	private Texture texture;
	private int textureIndex;

	private Animator animator;

	/**
	 * Creates a new ComponentAnimation.
	 *
	 * @param entity The entity this component is attached to.
	 * @param modelAnimated The animated model to use when animating and rendering.
	 * @param scale The scale of the entity.
	 * @param texture The diffuse texture for the entity.
	 * @param textureIndex What texture index this entity should renderObjects from (0 default).
	 */
	public ComponentAnimation(Entity entity, ModelAnimated modelAnimated, float scale, Texture texture, int textureIndex) {
		super(entity, ID);
		this.modelAnimated = modelAnimated;
		this.scale = scale;

		this.texture = texture;
		this.textureIndex = textureIndex;

		if (modelAnimated != null) {
			modelAnimated.getHeadJoint().calculateInverseBindTransform(Matrix4f.rotate(new Matrix4f(), new Vector3f(1.0f, 0.0f, 0.0f), (float) Math.toRadians(-90.0f), null));
			this.animator = new Animator(modelAnimated.getHeadJoint());
		}
	}

	/**
	 * Creates a new ComponentAnimation. From strings loaded from entity files.
	 *
	 * @param entity The entity this component is attached to.
	 * @param template The entity template to load data from.
	 */
	public ComponentAnimation(Entity entity, EntityTemplate template) {
		super(entity, ID);

		{
			String[] jointsData = template.getSectionData(ComponentAnimation.this, "Joints");
			List<Pair<Joint, List<String>>> allJoints = new ArrayList<>();

			int index = 0;
			String name = "";
			float[] localBindTransform = new float[16];
			List<String> children = new ArrayList<>();

			int id = 0;

			for (int i = 0; i < jointsData.length; i++) {
				switch (id) {
					case 0:
						index = Integer.parseInt(jointsData[i]);
						break;
					case 1:
						name = jointsData[i];
						break;
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 17:
						localBindTransform[id - 2] = Float.parseFloat(jointsData[i]);
						break;
					case 18:
						Collections.addAll(children, jointsData[i].split("|"));
						allJoints.add(new Pair<>(new Joint(index, name, new Matrix4f(localBindTransform)), new ArrayList<>(children)));
						index = 0;
						name = "";
						localBindTransform = new float[16];
						children = new ArrayList<>();
						id = 0;
						break;
				}

				id++;
			}

			List<JointData> jointDatas = new ArrayList<>();

			for (Pair<Joint, List<String>> value : allJoints) {
				JointData data = new JointData(value.getFirst().getIndex(), value.getFirst().getName(), value.getFirst().getLocalBindTransform());
				jointDatas.add(data);
			}

			int dataId = 0;

			for (JointData data : jointDatas) {
				Pair<Joint, List<String>> allData = allJoints.get(dataId);
				List<String> childrenName = allData.getSecond();

				for (JointData potentialChild : jointDatas) {
					for (String pn : childrenName) {
						if (!potentialChild.nameId.equals(data.nameId) && potentialChild.nameId.equals(pn)) {
							data.addChild(potentialChild);
						}
					}
				}

				dataId++;
			}

			this.modelAnimated = new ModelAnimated(
					new MeshData(
							EntityTemplate.toFloatArray(template.getSectionData(ComponentAnimation.this, "Vertices")),
							EntityTemplate.toFloatArray(template.getSectionData(ComponentAnimation.this, "TextureCoords")),
							EntityTemplate.toFloatArray(template.getSectionData(ComponentAnimation.this, "Normals")),
							EntityTemplate.toFloatArray(template.getSectionData(ComponentAnimation.this, "Tangents")),
							EntityTemplate.toIntArray(template.getSectionData(ComponentAnimation.this, "Indices")),
							EntityTemplate.toIntArray(template.getSectionData(ComponentAnimation.this, "VertexWeights")),
							EntityTemplate.toFloatArray(template.getSectionData(ComponentAnimation.this, "VertexWeights")),
							Float.parseFloat(template.getValue(this, "FurthestPoint"))
					),
					new JointsData(
							Integer.parseInt(template.getValue(this, "JointCount")),
							jointDatas.get(0)
					)
			);
		}

		{
			float animationLength = Float.parseFloat(template.getValue(this, "AnimationLength"));
			String[] animationData = template.getSectionData(ComponentAnimation.this, "Animation");

			List<KeyFrame> keyFrames = new ArrayList<>();

			float timeStamp = 0.0f;
			String name = "";
			Vector3f position = new Vector3f();
			Quaternion rotation = new Quaternion();

			int id = 0;

			for (int i = 0; i < animationData.length; i++) {
				switch (id) {
					case 0:
						timeStamp = Float.parseFloat(animationData[i]);
						break;
					case 1:
						name = animationData[i];
						break;
					case 2:
						position.x = Float.parseFloat(animationData[i]);
						break;
					case 3:
						position.y = Float.parseFloat(animationData[i]);
						break;
					case 4:
						position.z = Float.parseFloat(animationData[i]);
						break;
					case 5:
						rotation.x = Float.parseFloat(animationData[i]);
						break;
					case 6:
						rotation.y = Float.parseFloat(animationData[i]);
						break;
					case 7:
						rotation.z = Float.parseFloat(animationData[i]);
						break;
					case 8:
						rotation.w = Float.parseFloat(animationData[i]);
						boolean set = false;

						for (KeyFrame frame : keyFrames) {
							if (frame.getTimeStamp() == timeStamp) {
								frame.getJointKeyFrames().put(name, new JointTransform(new Vector3f(position), new Quaternion(rotation)));
								set = true;
							}
						}

						if (!set) {
							KeyFrame newFrame = new KeyFrame(timeStamp, new HashMap<>());
							newFrame.getJointKeyFrames().put(name, new JointTransform(new Vector3f(position), new Quaternion(rotation)));
							keyFrames.add(newFrame);
						}

						timeStamp = 0.0f;
						name = "";
						position = new Vector3f();
						rotation = new Quaternion();
						id = 0;
						break;
				}

				id++;
			}

			keyFrames.sort((KeyFrame p1, KeyFrame p2) -> (int) (p1.getTimeStamp() - p2.getTimeStamp()));
			KeyFrame[] frames = new KeyFrame[keyFrames.size()];

			for (int i = 0; i < frames.length; i++) {
				frames[i] = keyFrames.get(i);
			}

			Animation animation = new Animation(animationLength, frames);
			doAnimation(animation);
		}

		this.scale = Float.parseFloat(template.getValue(this, "Scale"));

		if (!template.getValue(this, "Texture").equals("null")) {
			this.texture = Texture.newTexture(new MyFile(template.getValue(this, "Texture"))).create();
			this.texture.setNumberOfRows(Integer.parseInt(template.getValue(this, "TextureNumRows")));
		}
	}

	@Override
	public void update() {
		if (animator != null) {
			animator.update();
		}
	}

	/**
	 * Instructs this entity to carry out a given animation.
	 *
	 * @param animation The animation to be carried out.
	 */
	public void doAnimation(Animation animation) {
		animator.doAnimation(animation);
	}

	/**
	 * Gets the scale for this model.
	 *
	 * @return The scale for this model.
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * Sets the scale for this model.
	 *
	 * @param scale The new scale.
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}

	/**
	 * Gets the animated model for this entity.
	 *
	 * @return The animated model for this entity.
	 */
	public ModelAnimated getModelAnimated() {
		return modelAnimated;
	}

	public void setModelAnimated(ModelAnimated modelAnimated) {
		if (this.modelAnimated != modelAnimated) {
			this.modelAnimated = modelAnimated;
			modelAnimated.getHeadJoint().calculateInverseBindTransform(Matrix4f.rotate(new Matrix4f(), new Vector3f(1.0f, 0.0f, 0.0f), (float) Math.toRadians(-90.0f), null));
			this.animator = new Animator(modelAnimated.getHeadJoint());
		}
	}

	/**
	 * Gets an array of the model-space transforms of all the joints (with the current animation pose applied) in the entity.
	 * The joints are ordered in the array based on their joint index.
	 * The position of each joint's transform in the array is equal to the joint's index.
	 *
	 * @return The array of model-space transforms of the joints in the current animation pose.
	 */
	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[modelAnimated.getJointsData().jointCount];
		addJointsToArray(modelAnimated.getHeadJoint(), jointMatrices);
		return jointMatrices;
	}

	/**
	 * This adds the current model-space transform of a joint (and all of its descendants) into an array of transforms.
	 * The joint's transform is added into the array at the position equal to the joint's index.
	 *
	 * @param headJoint The head joint to add children to.
	 * @param jointMatrices The matrices transformation to add with.
	 */
	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
		jointMatrices[headJoint.getIndex()] = headJoint.getAnimatedTransform();

		for (Joint childJoint : headJoint.getChildren()) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}

	/**
	 * Gets the diffuse texture for this entity.
	 *
	 * @return The diffuse texture for this entity.
	 */
	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void setAnimator(Animator animator) {
		this.animator = animator;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	/**
	 * Gets the textures coordinate offset that is used in rendering the model.
	 *
	 * @return The coordinate offset used in rendering.
	 */
	public Vector2f getTextureOffset() {
		int column = textureIndex % texture.getNumberOfRows();
		int row = textureIndex / texture.getNumberOfRows();
		return new Vector2f((float) row / (float) texture.getNumberOfRows(), (float) column / (float) texture.getNumberOfRows());
	}

	public Animator getAnimator() {
		return animator;
	}

	@Override
	public void dispose() {
	}
}
