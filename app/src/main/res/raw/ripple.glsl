uniform float time;
uniform vec2 resolution;
uniform vec2 rippleCenter;

half4 main(vec2 fragCoord) {
    vec2 uv = fragCoord / resolution;
    vec2 center = rippleCenter / resolution;

    vec2 pos = uv - center;
    float dist = length(pos);

    float ripple = 0.03 / dist * sin(20.0 * dist - time * 4.0);
    float fade = exp(-3.0 * dist) * (1.0 - time * 0.3);

    vec3 color = mix(
    vec3(0.0, 0.0, 0.1),
    vec3(0.3, 0.7, 1.0),
    clamp(ripple * fade, 0.0, 1.0)
    );

    return half4(color, 1.0);
}
