import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '4d157db31217611ef8310ac35cc772f4d85422236a26a670d327168766b31a3a') {
    pending.push(import('./chunks/chunk-61c51a9f7f8246aa16988be3a791504cf70596190f458b7c0194c2afc1a515a4.js'));
  }
  if (key === '54c06af978bade981985d1364dc07df59d2df33cba42bfafc2ddc2c68a57b452') {
    pending.push(import('./chunks/chunk-61c51a9f7f8246aa16988be3a791504cf70596190f458b7c0194c2afc1a515a4.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}