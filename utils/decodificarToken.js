export function decodificarTokenYSacarelRol(token) {
  
    if (!token) {
      console.error('No se encontró el token en el localStorage');
      return null;
    }
  
    // Divide el token en sus partes: encabezado, cuerpo y firma
    const [header, payload, signature] = token.split('.');
  
    // Decodifica el payload base64 y convierte a objeto JSON
    const decodedPayload = JSON.parse(atob(payload));
  
    // Extrae el rol del payload
    const role = decodedPayload.role;
  
    return role;
  }
  
  // Mostro el rol del usuario
  const role = decodificarTokenYSacarelRol();
  console.log('Rol del usuario:', role);
  