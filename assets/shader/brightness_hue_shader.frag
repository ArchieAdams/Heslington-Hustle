#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_brightness;
uniform float u_hue;

void main() {
    // Sample the texture
    vec4 texColor = texture2D(u_texture, v_texCoords) * v_color;

    // Adjust brightness
    texColor.rgb *= u_brightness;

    // Adjust hue
    float angle = u_hue * 3.1415926535897932384626433832795 / 180.0;
    float s = sin(angle);
    float c = cos(angle);
    mat3 hueMatrix = mat3(
        0.213 + 0.787*c - 0.213*s, 0.715 - 0.715*c - 0.715*s, 0.072 - 0.072*c + 0.928*s,
        0.213 - 0.213*c + 0.143*s, 0.715 + 0.285*c + 0.140*s, 0.072 - 0.072*c - 0.283*s,
        0.213 - 0.213*c - 0.787*s, 0.715 - 0.715*c + 0.715*s, 0.072 + 0.928*c + 0.072*s
    );
    texColor.rgb = hueMatrix * texColor.rgb;

    // Output final color
    gl_FragColor = texColor;
}
