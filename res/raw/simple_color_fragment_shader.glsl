precision mediump float;

uniform float u_Opacity; // Opacity of the color

varying vec4 v_Color;	// Input vertex 

void main() {
	gl_FragColor = v_Color * u_Opacity;
}

