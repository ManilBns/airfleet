#include <jni.h> //communication Java ↔ C
#include <stdio.h>
#include <stdlib.h>
#include "nativeLib_NativeLib.h" //contient le prototype des fonctions natives
#include <math.h>


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

// Fonction utilitaire : copier les champs depuis l'objet Java vers struct Avion C
Avion getAvion(JNIEnv *env, jobject jAvion) { //JNIEnv *env est la syntaxe JNI obligatoire pour appeler une fonction native depuis le pointeur JNIEnv *env
    Avion a;
    jclass cls = (*env)->GetObjectClass(env, jAvion); //récuperation de la classe Avion java
    jfieldID fid;
    //recuperation d'un field entier ( id )
    fid = (*env)->GetFieldID(env, cls, "id", "I"); //Dans la classe cls (java), donne-moi l’identifiant du champ qui s’appelle id et qui est de type entier (I)
    a.id = (*env)->GetIntField(env, jAvion, fid); //Dans l’objet Java jAvion, lis la valeur du champ entier identifié par fid

    //recuperation d'un champ string (fabricant)
    fid = (*env)->GetFieldID(env, cls, "fabricant", "Ljava/lang/String;"); //Dans la classe cls, donne-moi l’ID du champ String appelé fabricant
    jstring str = (jstring)(*env)->GetObjectField(env, jAvion, fid); //Dans l’objet Java jAvion, lis la valeur du champ string identifié par fid
    const char *cstr = (*env)->GetStringUTFChars(env, str, NULL); //Convertit le String Java → chaîne C
    snprintf(a.fabricant, sizeof(a.fabricant), "%s", cstr); //ex : a.fabricant = "Airbus" , snprintf protège contre le dépassement de mémoire
    (*env)->ReleaseStringUTFChars(env, str, cstr); //libérer la mémoire jni

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

// Fonction utilitaire : créer un objet java Avion à partir de struct Avion qui est en c
jobject createAvion(JNIEnv *env, jclass clsAvion, Avion a) {
    jmethodID constructor = (*env)->GetMethodID(env, clsAvion, "<init>", "(ILjava/lang/String;Ljava/lang/String;IIII)V"); //int string string int int int
    jstring fab = (*env)->NewStringUTF(env, a.fabricant); //Convertir les chaînes C en String Java ("Airbus" -> new String("Airbus") )
    jstring mod = (*env)->NewStringUTF(env, a.modele); //Convertir les chaînes C en String Java
    return (*env)->NewObject(env, clsAvion, constructor, a.id, fab, mod, a.capacite, a.autonomie, a.crashs, a.anneeService); //Créer l’objet Java Avion
}

// ----------------- TRI PAR CRASHS -----------------
JNIEXPORT jobjectArray JNICALL Java_nativeLib_NativeLib_trierParCrashs
  (JNIEnv *env, jobject obj, jobjectArray jAvions) {

    jsize len = (*env)->GetArrayLength(env, jAvions); //Récupérer la longueur du tableau Java
    Avion *avions = malloc(len * sizeof(Avion)); //Allouer un tableau C d’avions
    //convertir les avions java en c
    for(int i = 0; i < len; i++) {
        jobject jAvion = (*env)->GetObjectArrayElement(env, jAvions, i);
        avions[i] = getAvion(env, jAvion);
    }
    // Tri à bulles par crashs
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
    jclass clsAvion = (*env)->FindClass(env, "model/Avion"); //trouver la classe avions
    jobjectArray result = (*env)->NewObjectArray(env, len, clsAvion, NULL); //Créer le tableau Java de retour
    //creation des objet java triés
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

    jsize len = (*env)->GetArrayLength(env, jAvions); //la longueur du tableau java
    if(len == 0) return 0; //pour éviter la div sur zero
    double sum = 0;
    for(int i = 0; i < len; i++) {
        jobject jAvion = (*env)->GetObjectArrayElement(env, jAvions, i); //récupère l’objet Avion à l’index i
        Avion a = getAvion(env, jAvion); //convertit l’objet Java en struct C
        sum += a.crashs;
    }
    double result = sum / len / 100;
    // Arrondi à 4 décimales
    result = round(result * 10000.0) / 10000.0;
    return result;
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
    double result = sum / len;
    result = round(result * 10000.0) / 10000.0;
    return result;
}
