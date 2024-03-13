#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform sampler2D u_mask;

varying vec4 v_color;
varying vec2 v_texCoord0;

void main()
{
    vec4 texColor = texture2D(u_texture, v_texCoord0);
    vec4 mask = texture2D(u_mask, v_texCoord0);
    // Check if the alpha value of the mask is greater than 0
        if (mask.a > 0.0) {
            // Set the color to white for pixels with non-zero alpha in the mask
            gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
        } else {
            // Keep the original color for pixels with zero alpha in the mask
            gl_FragColor = texColor;
        }
}
