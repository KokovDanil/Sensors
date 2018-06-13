#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jfloat JNICALL
Java_dan_ko_sensors_GetAverageJNI_nativeGetAverage(JNIEnv *env, jclass type, jfloatArray array_,
                                                   jint size) {
    jfloat *array = env->GetFloatArrayElements(array_, NULL);

    if (size < 3){
        return 0;
    }
    else {
        float sum = 0;
        for (int i = 1; i < size - 1; i++) {
            sum += array[i];
        }
        env->ReleaseFloatArrayElements(array_, array, 0);

        return sum / (size - 2);
    }
}