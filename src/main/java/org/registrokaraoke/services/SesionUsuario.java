package org.registrokaraoke.services;

import org.registrokaraoke.models.Usuario;

public class SesionUsuario {
    public static Usuario usuarioLogeado;

    public static void setUsuarioLogeado(Usuario usuario) {
        usuarioLogeado = usuario;
    }

    public static Usuario getUsuarioLogeado() {
        return usuarioLogeado;
    }
}