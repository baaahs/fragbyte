uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

vec2 hash12(float p) {
    float n = sin(dot(vec2(p), vec2(41, 289)));
    return fract(vec2(262144, 32768)*n);
}

float wave(vec2 p, vec2 center, float freq, float speed, float offset){
    return sin(distance(p, center) * freq - time * speed + offset) * 0.5 + 0.5;
}

float calcWaves(vec2 p){
    const int steps = 2;
    const float rSteps = 1.0 / float(steps);

    float totalWave = 0.0;

    for (int i = 0; i < 2; i++){
        vec2 randPos = hash12(float(i) * 3.0) * 2.0 - 1.0;
        totalWave += wave(p, randPos, 50.0 , 3.0 + exp2(-float(i) * 0.25), 0.0);
    }

    return totalWave * rSteps;
}

vec3 normal(vec2 p){
    vec2 delta = 1.0 / resolution;

    float center = calcWaves(p);
    float ox = calcWaves(p + vec2(delta.x, 0.0));
    float oy = calcWaves(p + vec2(0.0, delta.y));

    float dx = (center - ox) / delta.x;
    float dy = (center - oy) / delta.y;

    return normalize(vec3(dx, dy, 1.0));
}

void main( void ) {

    vec2 position = gl_FragCoord.xy / resolution.xy;
    vec2 wposition = (position * 2.0 - 1.0);
    vec3 wVec = normalize(vec3(wposition, 1.0));

    vec3 color = vec3(0.0);
    vec3 normal = normal(wposition);
    color += vec3(calcWaves(wposition));

    gl_FragColor = vec4(color, 1.0 );

}