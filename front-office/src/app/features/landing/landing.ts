import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Meta, Title } from '@angular/platform-browser';

@Component({
  selector: 'fp-landing',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './landing.html',
})
export class LandingComponent implements OnInit {
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);

  ngOnInit(): void {
    this.title.setTitle('FacturePro Africa — Logiciel de facturation pour PME africaines');
    this.meta.addTags([
      { name: 'description', content: 'FacturePro Africa : créez et envoyez des factures professionnelles, acceptez Orange Money, MTN MoMo, partagez par WhatsApp. Essai gratuit 14 jours.' },
      { name: 'keywords', content: 'facturation Afrique, logiciel PME Burkina Faso, facture Mobile Money, Orange Money facturation, gestion financière Afrique' },
      { property: 'og:title', content: 'FacturePro Africa — Facturation pro pour PME africaines' },
      { property: 'og:description', content: 'Créez des factures, encaissez via Mobile Money, partagez par WhatsApp. Conçu pour les PME d\'Afrique.' },
      { property: 'og:type', content: 'website' },
      { name: 'twitter:card', content: 'summary_large_image' },
      { name: 'robots', content: 'index, follow' },
    ]);
  }

  readonly stats = [
    { value: '500+', label: 'PME actives' },
    { value: '12k+', label: 'Factures créées' },
    { value: '98%', label: 'Taux de satisfaction' },
    { value: '14j', label: 'Essai gratuit' },
  ];

  readonly pains = [
    { icon: '📄', text: 'Créer des factures manuellement sous Word ou Excel' },
    { icon: '📵', text: 'Envoyer des factures par SMS sans suivi de paiement' },
    { icon: '😤', text: 'Ne pas savoir qui a payé ou qui relancer' },
    { icon: '💸', text: 'Gérer les paiements Mobile Money sans traçabilité' },
    { icon: '📂', text: 'Archiver les factures dans des dossiers papier' },
    { icon: '🕐', text: 'Passer des heures à faire la comptabilité du mois' },
  ];

  readonly features = [
    { icon: '⚡', title: 'Factures en 30 secondes', desc: 'Sélectionnez le client, ajoutez les lignes, envoyez. Numérotation automatique et PDF propre.' },
    { icon: '💬', title: 'Envoi WhatsApp & Email', desc: 'Partagez la facture directement via WhatsApp en un clic. Email automatique avec template professionnel.' },
    { icon: '🟠', title: 'Mobile Money intégré', desc: 'Orange Money, MTN MoMo, Moov Money — enregistrez et suivez tous vos paiements africains.' },
    { icon: '📊', title: 'Tableau de bord financier', desc: 'Taux de recouvrement, montants en attente, factures en retard — vue complète en temps réel.' },
    { icon: '🔔', title: 'Relances automatiques', desc: 'Rappels automatiques aux clients pour les factures impayées. Plus besoin de relancer manuellement.' },
    { icon: '📱', title: 'Devis → Facture en 1 clic', desc: 'Convertissez un devis accepté en facture définitive instantanément, sans ressaisie.' },
    { icon: '🌍', title: 'Multi-devises & pays', desc: 'XOF, XAF, MAD, EUR, USD — gérez des clients dans toute l\'Afrique et au-delà.' },
    { icon: '🔒', title: 'Données sécurisées', desc: 'Architecture multi-tenant : vos données sont isolées et sécurisées. Conforme RGPD.' },
    { icon: '📥', title: 'Export & partage PDF', desc: 'Imprimez ou exportez vos factures en PDF de qualité professionnelle depuis n\'importe quel appareil.' },
  ];

  readonly steps = [
    { num: '1', title: 'Créez votre compte', desc: 'Inscrivez-vous en 2 minutes. Aucune carte bancaire requise pour l\'essai gratuit de 14 jours.' },
    { num: '2', title: 'Ajoutez vos clients', desc: 'Importez ou créez vos clients. Nom, email, téléphone, adresse — tout ce qu\'il faut.' },
    { num: '3', title: 'Facturez et encaissez', desc: 'Créez une facture, envoyez par WhatsApp ou email, enregistrez le paiement Mobile Money.' },
  ];

  readonly plans = [
    {
      name: 'Gratuit',
      price: '0 XOF',
      period: '14 jours d\'essai, puis limité',
      highlighted: false,
      cta: 'Commencer gratuitement',
      features: ['5 factures / mois', '2 clients', 'Export PDF', 'Support email'],
    },
    {
      name: 'Starter',
      price: '5 000 XOF',
      period: '/ mois · facturation annuelle',
      highlighted: true,
      cta: 'Démarrer l\'essai gratuit',
      features: ['Factures illimitées', 'Clients illimités', 'WhatsApp & Email', 'Mobile Money', 'Relances auto', 'Support prioritaire'],
    },
    {
      name: 'Pro',
      price: '12 000 XOF',
      period: '/ mois · tout inclus',
      highlighted: false,
      cta: 'Contacter les ventes',
      features: ['Tout Starter +', 'Multi-utilisateurs', 'API accès', 'Comptable invité', 'Rapports avancés', 'Onboarding dédié'],
    },
  ];

  readonly testimonials = [
    { name: 'Aminata Diallo', company: 'AD Consulting, Dakar', quote: 'Avant je passais 2h par semaine sur mes factures Excel. Maintenant 10 minutes. Mes clients reçoivent des factures professionnelles par WhatsApp.' },
    { name: 'Issouf Ouédraogo', company: 'ISO BTP, Ouagadougou', quote: 'Le suivi des paiements Orange Money est révolutionnaire pour mon activité. Je sais exactement ce qui a été payé et ce qui est en retard.' },
    { name: 'Marie-Claire Koffi', company: 'MCK Traiteur, Abidjan', quote: 'J\'ai converti 3 devis en factures la semaine dernière en 1 clic chacun. Un gain de temps incroyable pour une petite structure comme la mienne.' },
  ];
}
