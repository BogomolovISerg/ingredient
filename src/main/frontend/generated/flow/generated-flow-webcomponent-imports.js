import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/app-layout/src/vaadin-app-layout.js';
import '@vaadin/horizontal-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/app-layout/src/vaadin-drawer-toggle.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/tooltip/src/vaadin-tooltip.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/icon/src/vaadin-icon.js';
import '@vaadin/scroller/src/vaadin-scroller.js';
import '@vaadin/grid/src/vaadin-grid.js';
import '@vaadin/grid/src/vaadin-grid-column.js';
import '@vaadin/grid/src/vaadin-grid-sorter.js';
import '@vaadin/checkbox/src/vaadin-checkbox.js';
import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import 'Frontend/generated/jar-resources/flow-component-directive.js';
import 'Frontend/generated/jar-resources/gridConnector.ts';
import 'Frontend/generated/jar-resources/vaadin-grid-flow-selection-column.js';
import '@vaadin/grid/src/vaadin-grid-column-group.js';
import 'Frontend/generated/jar-resources/lit-renderer.ts';
import '@vaadin/context-menu/src/vaadin-context-menu.js';
import 'Frontend/generated/jar-resources/contextMenuConnector.js';
import 'Frontend/generated/jar-resources/contextMenuTargetConnector.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '7af15cd44618193236d09d23ac9ed5fe7fb02fbc7f53ff3eaafa7b32b27f4722') {
    pending.push(import('./chunks/chunk-34e6b7063876ffea284f569df47e808fa3e7ec12f97d81211e13e299cce8ffcd.js'));
  }
  if (key === 'b89f78fff445e24bee637d0a30b2a73cf74f5d0a616e0e065896b05ca4fbe00a') {
    pending.push(import('./chunks/chunk-34e6b7063876ffea284f569df47e808fa3e7ec12f97d81211e13e299cce8ffcd.js'));
  }
  if (key === 'cbac53af93a1f6fc41207d65cb00af037a52bbfd394af56310bc7af182d0d7c7') {
    pending.push(import('./chunks/chunk-34e6b7063876ffea284f569df47e808fa3e7ec12f97d81211e13e299cce8ffcd.js'));
  }
  if (key === '19bf88bfa27d0c128bab9a371d8c577117fe579077f2a9299eaca8aaccc0638b') {
    pending.push(import('./chunks/chunk-34e6b7063876ffea284f569df47e808fa3e7ec12f97d81211e13e299cce8ffcd.js'));
  }
  if (key === '17fe0b735fd7163fdd2e383496c2c5d5e6c2959749f1d26298e3ded1e60c18c4') {
    pending.push(import('./chunks/chunk-d47ad4561768ad60648c4934ee1fe6bdda1d86d7746185cb7b02211a17a758ba.js'));
  }
  if (key === '6899f6c192cb991ef04d09bc12b86b26fc8791455015d50d34e4467af32b5ee5') {
    pending.push(import('./chunks/chunk-d47ad4561768ad60648c4934ee1fe6bdda1d86d7746185cb7b02211a17a758ba.js'));
  }
  if (key === '72824cde5f59881f8b7b2ca10c2f7732d6401608f916a32c8b35983f2bfcde9d') {
    pending.push(import('./chunks/chunk-34e6b7063876ffea284f569df47e808fa3e7ec12f97d81211e13e299cce8ffcd.js'));
  }
  if (key === '66ae86c8fa931eaf5ca9106e0e70f8a2d751c1e10be95030e5b84eef9b36e6d3') {
    pending.push(import('./chunks/chunk-f6787743db10c4af9bac5776204a90e1fb15f237dc6d008d1e735bda626f6db5.js'));
  }
  if (key === '2de7f3b324f821d92ec91ed7a42c2295506d02af2c72d8c0f20768329b865d60') {
    pending.push(import('./chunks/chunk-34e6b7063876ffea284f569df47e808fa3e7ec12f97d81211e13e299cce8ffcd.js'));
  }
  if (key === '0c20206076cbd4d43c8ca93f62e6b327199990b0c8fd0894f49c84b53238e283') {
    pending.push(import('./chunks/chunk-f6787743db10c4af9bac5776204a90e1fb15f237dc6d008d1e735bda626f6db5.js'));
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