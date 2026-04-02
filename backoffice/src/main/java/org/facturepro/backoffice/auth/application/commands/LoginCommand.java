package org.facturepro.backoffice.auth.application.commands;

/**
 * Commande de connexion utilisateur.
 */
public record LoginCommand(String email, String password) {}