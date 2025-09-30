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
