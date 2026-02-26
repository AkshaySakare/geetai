// starfield.glsl
uniform float time;
uniform vec2 resolution;

float hash(float n) {
    return fract(sin(n) * 43758.5453);
}

vec3 getStar(float i, float time, vec2 uv) {
    float d = mod(i + time * 1.5, 100.0) / 100.0;
    float a = hash(i) * 6.2831;
    float r = sqrt(hash(i + 1.0));
    vec2 pos = r * vec2(cos(a), sin(a));
    pos *= 1.0 - d;
    float brightness = smoothstep(0.01, 0.0, length(uv - pos) * (1.0 + d * 10.0));
    return vec3(brightness);
}

half4 main(vec2 fragCoord) {
    vec2 uv = (fragCoord - 0.5 * resolution) / resolution.y;
    vec3 col = vec3(0.0);

    for (float i = 0.0; i < 100.0; i++) {
        col += getStar(i, time, uv);
    }

    return half4(col, 1.0);
}
