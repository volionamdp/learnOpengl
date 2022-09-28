package com.volio.basicopengles;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRUE;
import static android.opengl.GLES20.glCreateProgram;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;


public class EglUtil {
    private EglUtil() {
    }

    public static final int NO_TEXTURE = -1;
    private static final int FLOAT_SIZE_BYTES = 4;
    private static Bitmap bitmapDelete;


    public static int loadShader(final String strSource, final int iType) {
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(iType);
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.d("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(iShader));
            return 0;
        }
        return iShader;
    }

    public static int createProgram(final int vertexShader, final int pixelShader) throws GLException {
        final int program = glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Could not create program");
        }

        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, pixelShader);

        GLES20.glLinkProgram(program);
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GL_TRUE) {
            GLES20.glDeleteProgram(program);
            throw new RuntimeException("Could not link program");
        }
        return program;
    }

    public static void setupSampler(final int target, final int mag, final int min) {
        GLES20.glTexParameterf(target, GL_TEXTURE_MAG_FILTER, mag);
        GLES20.glTexParameterf(target, GL_TEXTURE_MIN_FILTER, min);
        GLES20.glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    public static int createBuffer(final float[] data) {
        return createBuffer(toFloatBuffer(data));
    }

    public static int createBuffer(final FloatBuffer data) {
        final int[] buffers = new int[1];
        GLES20.glGenBuffers(buffers.length, buffers, 0);
        updateBufferData(buffers[0], data);
        return buffers[0];
    }

    public static FloatBuffer toFloatBuffer(final float[] data) {
        final FloatBuffer buffer = ByteBuffer
                .allocateDirect(data.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(data).position(0);
        return buffer;
    }

    public static void updateBufferData(final int bufferName, final FloatBuffer data) {
        GLES20.glBindBuffer(GL_ARRAY_BUFFER, bufferName);
        GLES20.glBufferData(GL_ARRAY_BUFFER, data.capacity() * FLOAT_SIZE_BYTES, data, GL_STATIC_DRAW);
        GLES20.glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static int loadTexture(final Bitmap img, final int usedTexId, final boolean recycle) {
        int[] textures = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img,0);
            textures[0] = usedTexId;
        }
        if (recycle) {
            img.recycle();
        }
        return textures[0];
    }

    public static void updateTextures(int[] textures,int width, int height) {
        if (textures[0] <= 0){
            GLES20.glGenTextures(textures.length, textures, 0);
        }
        for (int mTextureId : textures) {
            // liên kết với kết cấu fbo vì chúng ta sẽ thực hiện thiết lập.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            // Đặt bộ lọc giảm để sử dụng màu của pixel gần nhất trong kết cấu làm màu của pixel sẽ được vẽ
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            // Đặt bộ lọc thu phóng để sử dụng một số màu có tọa độ gần nhất trong kết cấu và lấy màu pixel được vẽ thông qua thuật toán trung bình có trọng số
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            // Đặt sử lí viền khi texture thiếu
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            // Đặt sử lí viền khi texture thiếu
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            // unbind fbo texture.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
    }
    public static int[] createTextures(int width, int height, int count) {
        int[] textures = new int[count];
        GLES20.glGenTextures(textures.length, textures, 0);
        for (int mTextureId : textures) {
            // liên kết với kết cấu fbo vì chúng ta sẽ thực hiện thiết lập.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            // Đặt bộ lọc giảm để sử dụng màu của pixel gần nhất trong kết cấu làm màu của pixel sẽ được vẽ
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            // Đặt bộ lọc thu phóng để sử dụng một số màu có tọa độ gần nhất trong kết cấu và lấy màu pixel được vẽ thông qua thuật toán trung bình có trọng số
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            // Đặt sử lí viền khi texture thiếu
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            // Đặt sử lí viền khi texture thiếu
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            // unbind fbo texture.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
        return textures;
    }

    public static int[] deleteTextures(int[] textures) {
        int[] texturesDelete = new int[1];
        for (int texture : textures) {
            texturesDelete[0] = texture;
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
            GLES20.glDeleteTextures(texturesDelete.length, texturesDelete, 0);
        }
        Arrays.fill(textures, -1);
        return textures;
    }
    public static int deleteTextures(int texture) {
        if (bitmapDelete == null){
            bitmapDelete = Bitmap.createBitmap(1,1, Bitmap.Config.RGB_565);
        }
        if (texture>0) {
            loadTexture(bitmapDelete,texture,false);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
            GLES20.glDeleteTextures(1, new int[]{texture}, 0);
        }
        return -1;
    }

}
