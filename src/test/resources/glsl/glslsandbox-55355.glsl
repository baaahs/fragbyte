uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

void main( void ) {
    vec2 pos = gl_FragCoord.xy / resolution;
    float amnt = 5.0;
    float nd = 0.0;
    vec4 cbuff = vec4(0.0);

    for(int i=10; i<30;i++){

        nd =sin(3.03*0.3*pos.x + (i*0.1+sin(+time)*0.4) + time)*0.4+0.1 + pos.x;
        amnt = 1.0/abs(nd-pos.y)*0.01;

        cbuff += vec4(amnt, amnt*0.3 , amnt*pos.y, 081.0);
    }

    for(int i=0; i<1;i++){
        nd =sin(3.0*pos.y + i*10.7 + time)*80.3*(pos.y+87.3)+2.5;
        amnt = 1.0/abs(nd-pos.x)*0.015;

        cbuff += vec4(amnt*0.2, amnt*0.2 , amnt*pos.x, 1.0);
    }


    gl_FragColor = cbuff ;
}

