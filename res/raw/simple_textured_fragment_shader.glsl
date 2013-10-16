precision mediump float;

uniform float u_Opacity; // Opacity of the texture
uniform sampler2D u_Texture;

varying vec2 v_TexCoord;

void main() {
    gl_FragColor = texture2D(u_Texture, v_TexCoord);
    gl_FragColor.a *= u_Opacity;
}