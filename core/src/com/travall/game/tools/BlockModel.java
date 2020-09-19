package com.travall.game.tools;

import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.*;
import com.badlogic.gdx.graphics.g3d.model.data.*;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.*;

public class BlockModel implements Disposable {
    /** the materials of the model, used by nodes that have a graphical representation FIXME not sure if superfluous, allows
     * modification of materials without having to traverse the nodes **/
    public final Array<Material> materials = new Array();
    /** root nodes of the model **/
    public final Array<Node> nodes = new Array();
    /** animations of the model, modifying node transformations **/
    public final Array<Animation> animations = new Array();
    /** the meshes of the model **/
    public final Array<Mesh> meshes = new Array();
    /** parts of meshes, used by nodes that have a graphical representation FIXME not sure if superfluous, stored in Nodes as well,
     * could be useful to create bullet meshes **/
    public final Array<MeshPart> meshParts = new Array();
    /** Array of disposable resources like textures or meshes the BlockModel is responsible for disposing **/
    protected final Array<Disposable> disposables = new Array();

    /** Constructs a new BlockModel based on the {@link ModelData}. Texture files will be loaded from the internal file storage via an
     * {@link TextureProvider.FileTextureProvider}.
     * @param modelData the {@link ModelData} got from e.g. {@link ModelLoader} */
    public BlockModel (ModelData modelData) {
        this(modelData, new TextureProvider.FileTextureProvider());
    }
    public BlockModel () {
    }


    /** Constructs a new BlockModel based on the {@link ModelData}.
     * @param modelData the {@link ModelData} got from e.g. {@link ModelLoader}
     * @param textureProvider the {@link TextureProvider} to use for loading the textures */
    public BlockModel (ModelData modelData, TextureProvider textureProvider) {
        load(modelData, textureProvider);
    }


    protected void load (ModelData modelData, TextureProvider textureProvider) {
        loadMeshes(modelData.meshes);
        loadMaterials(modelData.materials, textureProvider);
        loadNodes(modelData.nodes);
        calculateTransforms();
    }

    private ObjectMap<NodePart, ArrayMap<String, Matrix4>> nodePartBones = new ObjectMap<NodePart, ArrayMap<String, Matrix4>>();

    protected void loadNodes (Iterable<ModelNode> modelNodes) {
        nodePartBones.clear();
        for (ModelNode node : modelNodes) {
            nodes.add(loadNode(node));
        }
        for (ObjectMap.Entry<NodePart, ArrayMap<String, Matrix4>> e : nodePartBones.entries()) {
            if (e.key.invBoneBindTransforms == null)
                e.key.invBoneBindTransforms = new ArrayMap<Node, Matrix4>(Node.class, Matrix4.class);
            e.key.invBoneBindTransforms.clear();
            for (ObjectMap.Entry<String, Matrix4> b : e.value.entries())
                e.key.invBoneBindTransforms.put(getNode(b.key), new Matrix4(b.value).inv());
        }
    }

    public void manageDisposable (Disposable disposable) {
        if (!disposables.contains(disposable, true)) disposables.add(disposable);
    }

    protected Node loadNode (ModelNode modelNode) {
        Node node = new Node();
        node.id = modelNode.id;

        if (modelNode.translation != null) node.translation.set(modelNode.translation);
        if (modelNode.rotation != null) node.rotation.set(modelNode.rotation);
        if (modelNode.scale != null) node.scale.set(modelNode.scale);
        // FIXME create temporary maps for faster lookup?
        if (modelNode.parts != null) {
            for (ModelNodePart modelNodePart : modelNode.parts) {
                MeshPart meshPart = null;
                Material meshMaterial = null;

                if (modelNodePart.meshPartId != null) {
                    for (MeshPart part : meshParts) {
                        if (modelNodePart.meshPartId.equals(part.id)) {
                            meshPart = part;
                            break;
                        }
                    }
                }

                if (modelNodePart.materialId != null) {
                    for (Material material : materials) {
                        if (modelNodePart.materialId.equals(material.id)) {
                            meshMaterial = material;
                            break;
                        }
                    }
                }

                if (meshPart == null || meshMaterial == null) throw new GdxRuntimeException("Invalid node: " + node.id);

                if (meshPart != null && meshMaterial != null) {
                    NodePart nodePart = new NodePart();
                    nodePart.meshPart = meshPart;
                    nodePart.material = meshMaterial;
                    node.parts.add(nodePart);
                    if (modelNodePart.bones != null) nodePartBones.put(nodePart, modelNodePart.bones);
                }
            }
        }

        if (modelNode.children != null) {
            for (ModelNode child : modelNode.children) {
                node.addChild(loadNode(child));
            }
        }

        return node;
    }

    protected void loadMeshes (Iterable<ModelMesh> meshes) {
        for (ModelMesh mesh : meshes) {
            convertMesh(mesh);
        }
    }

    protected void convertMesh (ModelMesh modelMesh) {
        int numIndices = 0;
        for (ModelMeshPart part : modelMesh.parts) {
            numIndices += part.indices.length;
        }
        boolean hasIndices = numIndices > 0;
        VertexAttributes attributes = new VertexAttributes(modelMesh.attributes);
        int numVertices = modelMesh.vertices.length / (attributes.vertexSize / 4);

        Mesh mesh = new Mesh(true, numVertices, numIndices, attributes);
        meshes.add(mesh);
        disposables.add(mesh);

        BufferUtils.copy(modelMesh.vertices, mesh.getVerticesBuffer(), modelMesh.vertices.length, 0);
        int offset = 0;
        mesh.getIndicesBuffer().clear();
        for (ModelMeshPart part : modelMesh.parts) {
            MeshPart meshPart = new MeshPart();
            meshPart.id = part.id;
            meshPart.primitiveType = part.primitiveType;
            meshPart.offset = offset;
            meshPart.size = hasIndices ? part.indices.length : numVertices;
            meshPart.mesh = mesh;
            if (hasIndices) {
                mesh.getIndicesBuffer().put(part.indices);
            }
            offset += meshPart.size;
            meshParts.add(meshPart);
        }
        mesh.getIndicesBuffer().position(0);
        for (MeshPart part : meshParts)
            part.update();
    }

    protected void loadMaterials (Iterable<ModelMaterial> modelMaterials, TextureProvider textureProvider) {
        for (ModelMaterial mtl : modelMaterials) {
            this.materials.add(convertMaterial(mtl, textureProvider));
        }
    }

    protected Material convertMaterial (ModelMaterial mtl, TextureProvider textureProvider) {
        Material result = new Material();
        result.id = mtl.id;
        if (mtl.ambient != null) result.set(new ColorAttribute(ColorAttribute.Ambient, mtl.ambient));
        if (mtl.diffuse != null) result.set(new ColorAttribute(ColorAttribute.Diffuse, mtl.diffuse));
        if (mtl.specular != null) result.set(new ColorAttribute(ColorAttribute.Specular, mtl.specular));
        if (mtl.emissive != null) result.set(new ColorAttribute(ColorAttribute.Emissive, mtl.emissive));
        if (mtl.reflection != null) result.set(new ColorAttribute(ColorAttribute.Reflection, mtl.reflection));
        if (mtl.shininess > 0f) result.set(new FloatAttribute(FloatAttribute.Shininess, mtl.shininess));
        if (mtl.opacity != 1.f) result.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, mtl.opacity));

        ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();

        // FIXME uvScaling/uvTranslation totally ignored
        if (mtl.textures != null) {
            for (ModelTexture tex : mtl.textures) {
                Texture texture;
                if (textures.containsKey(tex.fileName)) {
                    texture = textures.get(tex.fileName);
                } else {
                    texture = textureProvider.load(tex.fileName);
                    textures.put(tex.fileName, texture);
                    disposables.add(texture);
                }

                TextureDescriptor descriptor = new TextureDescriptor(texture);
                descriptor.minFilter = texture.getMinFilter();
                descriptor.magFilter = texture.getMagFilter();
                descriptor.uWrap = texture.getUWrap();
                descriptor.vWrap = texture.getVWrap();

                float offsetU = tex.uvTranslation == null ? 0f : tex.uvTranslation.x;
                float offsetV = tex.uvTranslation == null ? 0f : tex.uvTranslation.y;
                float scaleU = tex.uvScaling == null ? 1f : tex.uvScaling.x;
                float scaleV = tex.uvScaling == null ? 1f : tex.uvScaling.y;

                switch (tex.usage) {
                    case ModelTexture.USAGE_DIFFUSE:
                        result.set(new TextureAttribute(TextureAttribute.Diffuse, descriptor, offsetU, offsetV, scaleU, scaleV));
                        break;
                    case ModelTexture.USAGE_SPECULAR:
                        result.set(new TextureAttribute(TextureAttribute.Specular, descriptor, offsetU, offsetV, scaleU, scaleV));
                        break;
                    case ModelTexture.USAGE_BUMP:
                        result.set(new TextureAttribute(TextureAttribute.Bump, descriptor, offsetU, offsetV, scaleU, scaleV));
                        break;
                    case ModelTexture.USAGE_NORMAL:
                        result.set(new TextureAttribute(TextureAttribute.Normal, descriptor, offsetU, offsetV, scaleU, scaleV));
                        break;
                    case ModelTexture.USAGE_AMBIENT:
                        result.set(new TextureAttribute(TextureAttribute.Ambient, descriptor, offsetU, offsetV, scaleU, scaleV));
                        break;
                    case ModelTexture.USAGE_EMISSIVE:
                        result.set(new TextureAttribute(TextureAttribute.Emissive, descriptor, offsetU, offsetV, scaleU, scaleV));
                        break;
                    case ModelTexture.USAGE_REFLECTION:
                        result.set(new TextureAttribute(TextureAttribute.Reflection, descriptor, offsetU, offsetV, scaleU, scaleV));
                        break;
                }
            }
        }

        return result;
    }

    @Override
    public void dispose () {
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
    }

    public void calculateTransforms () {
        final int n = nodes.size;
        for (int i = 0; i < n; i++) {
            nodes.get(i).calculateTransforms(true);
        }
        for (int i = 0; i < n; i++) {
            nodes.get(i).calculateBoneTransforms(true);
        }
    }

    /** @param id The ID of the node to fetch.
     * @return The {@link Node} with the specified id, or null if not found. */
    public Node getNode (final String id) {
        return getNode(id, true);
    }

    /** @param id The ID of the node to fetch.
     * @param recursive false to fetch a root node only, true to search the entire node tree for the specified node.
     * @return The {@link Node} with the specified id, or null if not found. */
    public Node getNode (final String id, boolean recursive) {
        return getNode(id, recursive, false);
    }

    /** @param id The ID of the node to fetch.
     * @param recursive false to fetch a root node only, true to search the entire node tree for the specified node.
     * @param ignoreCase whether to use case sensitivity when comparing the node id.
     * @return The {@link Node} with the specified id, or null if not found. */
    public Node getNode (final String id, boolean recursive, boolean ignoreCase) {
        return Node.getNode(nodes, id, recursive, ignoreCase);
    }
}
