package com.volio.basicopengles

class test3 {
    val test =
        "#define MUSIC_REACTION 0.2\\n" +
                "#define m_pi 3.14159265359\\n" +
                "#define pi2 (m_pi * 2.0)\\n" +
                "#define halfPi (m_pi * 0.5)\",\n"

    val contentFunction0 =
        "mediump float N2(mediump vec2 p){\n   " +
                " p = mod(p, vec2(1456.2346));\n   " +
                " mediump vec3 p3  = fract(vec3(p.xyx) * vec3(443.897, 441.423, 437.195));\n  " +
                "  p3 += dot(p3, p3.yzx + 19.19);\n  " +
                "  return fract((p3.x + p3.y) * p3.z);\n" +
                "}" +
                "\n\n\n" +
                "mediump float CosineInterpolate(mediump float y1, mediump float y2, mediump float t){\n" +
                "    mediump float mu = (1.0 -cos(t * m_pi)) * 0.5;\n" +
                "    return (y1 * (1.0 - mu) + y2 * mu);\n" +
                "}" +
                "\n\n" +
                "mediump float Noise2(mediump vec2 uv)\n" +
                "{\n    mediump vec2 corner = floor(uv);\n" +
                "    mediump float c00 = N2(corner + vec2(0.0, 0.0));\n" +
                "    mediump float c01 = N2(corner + vec2(0.0, 1.0));\n" +
                "    mediump float c11 = N2(corner + vec2(1.0, 1.0));\n" +
                "    mediump float c10 = N2(corner + vec2(1.0, 0.0));\n\n" +
                "    mediump vec2 diff = fract(uv);\n\n" +
                "    return CosineInterpolate(CosineInterpolate(c00, c10, diff.x), CosineInterpolate(c01, c11, diff.x), diff.y);\n" +
                "}\n\n" +
                "mediump float LineNoise(mediump float x, mediump float t){\n" +
                "    mediump float n = Noise2(vec2(x * 0.6, t * 0.2));\n" +
                "    return n - (1.0) * 0.5;\n" +
                "}\n\n\n" +
                "mediump float line(mediump vec2 uv, mediump float t, mediump float scroll){\n" +
                "    mediump float ax = abs(uv.x);\n" +
                "    uv.y *= 0.5 + ax * ax * 0.3;\n" +
                "    uv.x += iTime * scroll;\n" +
                "    mediump float n1 = LineNoise(uv.x, t);\n" +
                "    mediump float n2 = LineNoise(uv.x + 0.5, t + 10.0) * 2.0;\n" +
                "    mediump float ay = abs(uv.y - n1);\n" +
                "    mediump float lum = smoothstep(0.02, 0.00, ay) * 1.5;\n" +
                "    lum += smoothstep(1.5, 0.00, ay) * 0.1;\n" +
                "    mediump float r = (uv.y - n1) / (n2 - n1);\n" +
                "    mediump float h = clamp(1.0 - r, 0.0, 1.0);\n" +
                "    if (r > 0.0) lum = max(lum, h * h * 0.7);\n " +
                "   return lum;\n" +
                "}"


    val contentVisualizer0 =
        "uv = (2.0*gl_FragCoord.xy-iResolution.xy)/iResolution.y;\n" +
                "    mediump float xWave = gl_FragCoord.x / iResolution.x;\n" +
                "    mediump float wave = texture2D(iChannel0, vec2(xWave * 0.2, 1.0)).r * sin(iTime * 0.2) * MUSIC_REACTION;\n" +
                "    mediump float wave1 = texture2D(iChannel0, vec2(xWave * 0.2 + 0.2, 1.0)).r * sin(iTime * 0.2 + 0.5) * MUSIC_REACTION;\n" +
                "    mediump float wave2 = texture2D(iChannel0, vec2(xWave * 0.2 + 0.4, 1.0)).r * sin(iTime * 0.2 + 1.0) * MUSIC_REACTION;\n" +
                "    mediump float wave3 = texture2D(iChannel0, vec2(xWave * 0.2 + 0.6, 1.0)).r * sin(iTime * 0.2 + 1.5) * MUSIC_REACTION;\n" +
                "    mediump float wave4 = texture2D(iChannel0, vec2(xWave * 0.2 + 0.8, 1.0)).r * sin(iTime * 0.2 + 2.0) * MUSIC_REACTION;\n" +
                "    mediump float lum = line(uv * vec2(2.0, 1.0)+  vec2(0.0, wave), iTime * 0.3, 0.1) * 0.6;\n" +
                "    lum += line(uv * vec2(1.5, 0.9) +  vec2(0.33, wave1), iTime * 0.5 + 45.0, 0.15) * 0.5;\n" +
                "    lum += line(uv * vec2(1.3, 1.2) +  vec2(0.66, wave2), iTime * 0.4 + 67.3, 0.2) * 0.3;\n" +
                "    lum += line(uv * vec2(1.5, 1.15) +  vec2(0.8, wave3), iTime * 0.77 + 1235.45, 0.23) * 0.43;\n" +
                "    lum += line(uv * vec2(1.5, 1.15) +  vec2(0.8, wave4), iTime * 0.77 + 456.45, 0.3) * 0.25;\n " +
                "    mediump float ax = abs(uv.x);\n" +
                "    lum += ax * ax * 0.05;\n" +
                "    mediump vec3 col;\n" +
                "    mediump float x = uv.x * 1.2 + iTime * 0.2;\n" +
                "    mediump vec3 hue = (sin(vec3(x, x + pi2 * 0.33, x + pi2 * 0.66)) + vec3(1.0)) * 0.7;\n" +
                "    mediump float thres = 0.7;\n" +
                "    if (lum < thres){\n" +
                "        col = hue * lum / thres;\n" +
                "    } else {\n" +
                "        col = vec3(1.0) - (vec3(1.0 - (lum - thres)) * (vec3(1.0) - hue));\n" +
                "    }\n" +
                "    visualizer = col;"


    //audio
    val contentFunction=
        "mediump float noise3D(mediump vec3 p){\n" +
                "    return fract(sin(dot(p, vec3(12.9898, 78.233, 12.7378))) * 43758.5453)*2.0-1.0;\n" +
                "}\n\n" +
                "mediump vec3 mixc(mediump vec3 col1, mediump vec3 col2, mediump float v){\n" +
                "    v = clamp(v, 0.0, 1.0);\n" +
                "    return col1+v*(col2-col1);\n" +
                "}"

    val contentVisualizer =
        "uv = gl_FragCoord.xy / iResolution.xy;\n" +
                "    mediump vec2 p = uv*2.0-1.0;\n" +
                "    p.x*=iResolution.x/iResolution.y;\n" +
                "    p.y+=0.5;\n\n" +
                "    mediump vec3 col = vec3(0.0);\n" +
                "    mediump vec3 ref = vec3(0.0);\n\n" +
                "    mediump float nBands = 48.0;\n" +
                "    mediump float i = floor(uv.x*nBands);\n" +
                "    mediump float f = fract(uv.x*nBands);\n" +
                "    mediump float band = i/nBands;\n" +
                "    band *= band*band;\n " +
                "       band = band*0.995;\n" +
                "    band += 0.005;\n" +
                "    mediump float s = texture2D(iChannel0, vec2(band, 0.25)).x;\n\n" +
                "    /* Gradient colors and amount here */\n" +
                "    const mediump int nColors = 4;\n " +
                "   mediump vec3 colors[nColors];\n" +
                "    colors[0] = vec3(0.0, 0.0, 1.0);\n" +
                "    colors[1] = vec3(0.0, 1.0, 1.0);\n" +
                "    colors[2] = vec3(1.0, 1.0, 0.0);\n" +
                "    colors[3] = vec3(1.0, 0.0, 0.0);\n\n" +
                "    mediump vec3 gradCol = colors[0];\n " +
                "   mediump float n = float(nColors)-1.0;\n" +
                "    for (mediump int i = 1; i < nColors; i++){\n" +
                "        gradCol = mixc(gradCol, colors[i], (s-float(i-1)/n)*n);\n" +
                "    }\n " +
                "   col += vec3(1.0-smoothstep(0.0, 0.01, p.y-s*1.5));\n " +
                "   col *= gradCol;\n" +
                "    ref += vec3(1.0-smoothstep(0.0, -0.01, p.y+s*1.5));\n" +
                "    ref*= gradCol*smoothstep(-0.5, 0.5, p.y);\n" +
                "    col = mix(ref, col, smoothstep(-0.01, 0.01, p.y));\n" +
                "    col *= smoothstep(0.125, 0.375, f);\n" +
                "    col *= smoothstep(0.875, 0.625, f);\n " +
                "   col = clamp(col, 0.0, 1.0);\n" +
                "    mediump float dither = noise3D(vec3(p, iTime))*2.0/256.0;\n " +
                "   col += dither;\n" +
                "   visualizer = col;"


}

