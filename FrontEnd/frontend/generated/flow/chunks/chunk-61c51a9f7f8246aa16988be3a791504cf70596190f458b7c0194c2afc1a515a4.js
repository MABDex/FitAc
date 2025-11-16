import '@vaadin/tooltip/theme/lumo/vaadin-tooltip.js';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/icon/theme/lumo/vaadin-icon.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/horizontal-layout/theme/lumo/vaadin-horizontal-layout.js';
import '@vaadin/button/theme/lumo/vaadin-button.js';
import 'Frontend/generated/jar-resources/buttonFunctions.js';
import '@vaadin/text-area/theme/lumo/vaadin-text-area.js';
import '@vaadin/notification/theme/lumo/vaadin-notification.js';
import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { css, unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin';
import $cssFromFile_0 from 'Frontend/styles/shared-styles.css?inline';

injectGlobalCss($cssFromFile_0.toString(), 'CSSImport end', document);