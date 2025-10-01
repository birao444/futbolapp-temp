const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

// Cada vez que se cree un usuario en Authentication
exports.asignarRolInicial = functions.auth.user().onCreate((user) => {
  const userDoc = {
    name: user.displayName || "",
    email: user.email,
    role: "pendiente", // 🚦 rol inicial
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
  };
  return admin.firestore().collection("users").doc(user.uid).set(userDoc);
});

// 👉 Función para asignar un rol a un usuario
exports.asignarRol = functions.https.onCall(async (data, context) => {
  // Verifica que el usuario que llama sea admin_entrenador
  if (!context.auth || context.auth.token.role !== "admin_entrenador") {
    throw new functions.https.HttpsError(
      "permission-denied",
      "No tienes permisos para asignar roles.",
    );
  }

  const uid = data.uid; // UID del usuario a modificar
  const nuevoRol = data.role; // "entrenador", "jugador", "padres"

  // Asigna el rol como custom claim
  await admin.auth().setCustomUserClaims(uid, {role: nuevoRol});

  return {message: `✅ Rol ${nuevoRol} asignado a usuario ${uid}`};
});
