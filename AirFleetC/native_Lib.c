#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include "nativeLib_NativeLib.h"

// Une structure Avion simplifiée pour manipuler en C
typedef struct {
    int id;
    char fabricant[50];
    char modele[50];
    int capacite;
    int autonomie;
    int crashs;
    int anneeService;
} Avion;

// Fonction utilitaire : copier les champs depuis jobject Java vers struct Avion C
Avion getAvion(JNIEnv *env, jobject jAvion) {
    Avion a;
    jclass cls = (*env)->GetObjectClass(env, jAvion);

    jfieldID fid;

    fid = (*env)->GetFieldID(env, cls, "id", "I");
    a.id = (*env)->GetIntField(env, jAvion, fid);

    fid = (*env)->GetFieldID(env, cls, "fabricant", "Ljava/lang/String;");
    jstring str = (jstring)(*env)->GetObjectField(env, jAvion, fid);
    const char *cstr = (*env)->GetStringUTFChars(env, str, NULL);
    snprintf(a.fabricant, sizeof(a.fabricant), "%s", cstr);
    (*env)->ReleaseStringUTFChars(env, str, cstr);

    fid = (*env)->GetFieldID(env, cls, "modele", "Ljava/lang/String;");
    str = (jstring)(*env)->GetObjectField(env, jAvion, fid);
    cstr = (*env)->GetStringUTFChars(env, str, NULL);
    snprintf(a.modele, sizeof(a.modele), "%s", cstr);
    (*env)->ReleaseStringUTFChars(env, str, cstr);

    fid = (*env)->GetFieldID(env, cls, "capacite", "I");
    a.capacite = (*env)->GetIntField(env, jAvion, fid);

    fid = (*env)->GetFieldID(env, cls, "autonomie", "I");
    a.autonomie = (*env)->GetIntField(env, jAvion, fid);

    fid = (*env)->GetFieldID(env, cls, "crashs", "I");
    a.crashs = (*env)->GetIntField(env, jAvion, fid);

    fid = (*env)->GetFieldID(env, cls, "anneeService", "I");
    a.anneeService = (*env)->GetIntField(env, jAvion, fid);

    return a;
}

// Fonction utilitaire : créer un jobject Avion à partir de struct Avion
jobject createAvion(JNIEnv *env, jclass clsAvion, Avion a) {
    jmethodID constructor = (*env)->GetMethodID(env, clsAvion, "<init>", "(ILjava/lang/String;Ljava/lang/String;IIII)V");
    jstring fab = (*env)->NewStringUTF(env, a.fabricant);
    jstring mod = (*env)->NewStringUTF(env, a.modele);
    return (*env)->NewObject(env, clsAvion, constructor, a.id, fab, mod, a.capacite, a.autonomie, a.crashs, a.anneeService);
}

// ----------------- TRI PAR CRASHS -----------------
JNIEXPORT jobjectArray JNICALL Java_nativeLib_NativeLib_trierParCrashs
  (JNIEnv *env, jobject obj, jobjectArray jAvions) {

    jsize len = (*env)->GetArrayLength(env, jAvions);
    Avion *avions = malloc(len * sizeof(Avion));
    for(int i = 0; i < len; i++) {
        jobject jAvion = (*env)->GetObjectArrayElement(env, jAvions, i);
        avions[i] = getAvion(env, jAvion);
    }

    // Tri simple (bubble sort) par crashs
    for(int i = 0; i < len-1; i++) {
        for(int j = 0; j < len-i-1; j++) {
            if(avions[j].crashs > avions[j+1].crashs) {
                Avion tmp = avions[j];
                avions[j] = avions[j+1];
                avions[j+1] = tmp;
            }
        }
    }

    // Création du tableau Java pour retourner
    jclass clsAvion = (*env)->FindClass(env, "model/Avion");
    jobjectArray result = (*env)->NewObjectArray(env, len, clsAvion, NULL);
    for(int i = 0; i < len; i++) {
        jobject jAvion = createAvion(env, clsAvion, avions[i]);
        (*env)->SetObjectArrayElement(env, result, i, jAvion);
    }

    free(avions);
    return result;
}

// ----------------- TRI PAR AUTONOMIE -----------------
JNIEXPORT jobjectArray JNICALL Java_nativeLib_NativeLib_trierParAutonomie
  (JNIEnv *env, jobject obj, jobjectArray jAvions) {
    jsize len = (*env)->GetArrayLength(env, jAvions);
    Avion *avions = malloc(len * sizeof(Avion));
    for(int i = 0; i < len; i++) {
        jobject jAvion = (*env)->GetObjectArrayElement(env, jAvions, i);
        avions[i] = getAvion(env, jAvion);
    }

    // Tri simple par autonomie
    for(int i = 0; i < len-1; i++) {
        for(int j = 0; j < len-i-1; j++) {
            if(avions[j].autonomie > avions[j+1].autonomie) {
                Avion tmp = avions[j];
                avions[j] = avions[j+1];
                avions[j+1] = tmp;
            }
        }
    }

    jclass clsAvion = (*env)->FindClass(env, "model/Avion");
    jobjectArray result = (*env)->NewObjectArray(env, len, clsAvion, NULL);
    for(int i = 0; i < len; i++) {
        jobject jAvion = createAvion(env, clsAvion, avions[i]);
        (*env)->SetObjectArrayElement(env, result, i, jAvion);
    }

    free(avions);
    return result;
}

// ----------------- MOYENNE CRASHS -----------------
JNIEXPORT jdouble JNICALL Java_nativeLib_NativeLib_moyenneCrashs
  (JNIEnv *env, jobject obj, jobjectArray jAvions) {

    jsize len = (*env)->GetArrayLength(env, jAvions);
    if(len == 0) return 0;

    double sum = 0;
    for(int i = 0; i < len; i++) {
        jobject jAvion = (*env)->GetObjectArrayElement(env, jAvions, i);
        Avion a = getAvion(env, jAvion);
        sum += a.crashs;
    }
    return sum / len;
}

// ----------------- MOYENNE AUTONOMIE -----------------
JNIEXPORT jdouble JNICALL Java_nativeLib_NativeLib_moyenneAutonomie
  (JNIEnv *env, jobject obj, jobjectArray jAvions) {

    jsize len = (*env)->GetArrayLength(env, jAvions);
    if(len == 0) return 0;

    double sum = 0;
    for(int i = 0; i < len; i++) {
        jobject jAvion = (*env)->GetObjectArrayElement(env, jAvions, i);
        Avion a = getAvion(env, jAvion);
        sum += a.autonomie;
    }
    return sum / len;
}
