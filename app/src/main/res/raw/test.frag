#extension GL_OES_standard_derivatives : enable
varying highp vec2 fragCoord;
uniform highp vec3 iResolution;
uniform sampler2D iChannel0;
uniform sampler2D iChannel1;
uniform sampler2D iChannel2;
uniform sampler2D iChannel3;
uniform sampler2D iChannel4;
uniform highp float iTime;
uniform highp float translatePercent;
uniform highp int quickTransition;
const highp float PI = 3.141592653589793;


highp vec3 get2DTexture(sampler2D sam, highp vec2 uv) {
    return texture2D(sam, uv).rgb;
}

highp vec4 get2DBeatTexture(sampler2D sam, highp vec2 uv) {
    return texture2D(sam, uv).rgba;
}


mediump float noise3D(mediump vec3 p){
    return fract(sin(dot(p, vec3(12.9898, 78.233, 12.7378))) * 43758.5453)*2.0-1.0;
}

mediump vec3 mixc(mediump vec3 col1, mediump vec3 col2, mediump float v){
    v = clamp(v, 0.0, 1.0);
    return col1+v*(col2-col1);
}

highp vec3 drawVisualizer(highp vec2 uv, highp vec3 visualizer){
    uv = gl_FragCoord.xy / iResolution.xy;
    mediump vec2 p = uv*2.0-1.0;
    p.x*=iResolution.x/iResolution.y;
    p.y+=0.5;

    mediump vec3 col = vec3(0.0);
    mediump vec3 ref = vec3(0.0);

    mediump float nBands = 48.0;
    mediump float i = floor(uv.x*nBands);
    mediump float f = fract(uv.x*nBands);
    mediump float band = i/nBands;
    band *= band*band;
    band = band*0.995;
    band += 0.005;
    mediump float s = texture2D(iChannel0, vec2(band, 0.25)).x;

    /* Gradient colors and amount here */
    const mediump int nColors = 4;
    mediump vec3 colors[nColors];
    colors[0] = vec3(0.0, 0.0, 1.0);
    colors[1] = vec3(0.0, 1.0, 1.0);
    colors[2] = vec3(1.0, 1.0, 0.0);
    colors[3] = vec3(1.0, 0.0, 0.0);

    mediump vec3 gradCol = colors[0];
    mediump float n = float(nColors)-1.0;
    for (mediump int i = 1; i < nColors; i++){
        gradCol = mixc(gradCol, colors[i], (s-float(i-1)/n)*n);
    }
    col += vec3(1.0-smoothstep(0.0, 0.01, p.y-s*1.5));
    col *= gradCol;
    ref += vec3(1.0-smoothstep(0.0, -0.01, p.y+s*1.5));
    ref*= gradCol*smoothstep(-0.5, 0.5, p.y);
    col = mix(ref, col, smoothstep(-0.01, 0.01, p.y));
    col *= smoothstep(0.125, 0.375, f);
    col *= smoothstep(0.875, 0.625, f);
    col = clamp(col, 0.0, 1.0);
    mediump float dither = noise3D(vec3(p, iTime))*2.0/256.0;
    col += dither;
    visualizer = col;
    return visualizer;
}
highp vec3 drawBackground(highp vec2 uv, highp vec3 background){
    uv = vec2(gl_FragCoord.x / iResolution.x, 1.0 - gl_FragCoord.y / iResolution.y);
    highp vec3 effectToBackground = vec3(0.0);
    bool isRGBEffect = false;

    if (quickTransition == 1){
        background = get2DTexture(iChannel1, uv);
    } else {
        highp float progress = translatePercent;
        if (progress < 0.5){
            background = get2DTexture(iChannel1, uv);
        } else {
            background = get2DTexture(iChannel3, uv);
        }
    }
    if (isRGBEffect){
        if (translatePercent == 0.0 || translatePercent == 1.0){
            return effectToBackground;
        } else {
            return background;
        }
    } else {
        if (effectToBackground.xyz == vec3(0.)){
            return background;
        } else {
            return effectToBackground*background;
        }
    }
}
highp vec3 drawParticle(highp vec2 uv, highp vec3 particle){

    return particle;
}
highp vec3 drawThumb(highp vec2 uv, highp vec3 visualizer){
    uv = vec2(fragCoord.x, 1.0 - fragCoord.y);


    return visualizer;
}
highp vec3 drawWatermark(highp vec2 uv, highp vec3 visualizer){

    return visualizer;
}
void main() {
    highp vec2 uv;
    highp vec3 visualizer = vec3(0., 0., 0.);
    highp vec3 background = vec3(0., 0., 0.);
    highp vec3 particle = vec3(0., 0., 0.);
    visualizer = drawVisualizer(uv, visualizer);
    background = drawBackground(uv, background);
    particle = drawParticle(uv, particle);
    background += particle;
    visualizer += background;
    visualizer = drawThumb(uv, visualizer);
    visualizer = drawWatermark(uv, visualizer);
    gl_FragColor = vec4(visualizer, 1.0);
}
