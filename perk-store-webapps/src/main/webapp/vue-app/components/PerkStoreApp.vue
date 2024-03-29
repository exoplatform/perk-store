<template>
  <v-app 
    id="PerkStoreApp"
    class="transparent VuetifyApp"
    flat>
    <main v-if="loading">
      <v-toolbar color="transparent" flat>
        <v-spacer />
        <v-progress-circular
          color="primary"
          class="mb-2"
          indeterminate />
        <v-spacer />
      </v-toolbar>
    </main>
    <main v-else-if="isApplicationEnabled">
      <v-layout justify-center>
        <v-flex xs12>
          <v-toolbar
            :class="productFilterMenu && 'display-on-top'"
            color="white"
            class="application-toolbar"
            flat
            dense>
            <v-toolbar-title class="d-flex mt-1">
              <v-btn
                v-show="displayFilterButton"
                id="perkStoreAppMenuDisplayFilterButton"
                :title="$t('exoplatform.perkstore.button.displayFilters')"
                icon
                text
                @click="showFilters">
                <v-icon color="primary">
                  fa-filter
                </v-icon>
              </v-btn>
              <span>{{ $t('exoplatform.perkstore.title.perkStoreApplication') }}</span>
              <template v-if="displayProductDetails && selectedProduct">
              </template>
              <template v-else-if="displayProductForm && selectedProduct && selectedProduct.id">
                - <span class="ml-2 primary--text">{{ selectedProduct.title }}</span>
              </template>
              <template v-else-if="displayProductForm && selectedProduct">
                - {{ $t('exoplatform.perkstore.title.createNewProduct') }}
              </template>
              <template v-else-if="displayProductOrders && selectedProduct && selectedOrderId">
                - {{ $t('exoplatform.perkstore.title.order') }} <span class="ml-2 primary--text">#{{ selectedOrderId }}</span> : <span class="ml-2 primary--text">{{ selectedProduct.title }}</span>
              </template>
              <template v-else-if="displayProductOrders && canEditSelectedProduct">
                - {{ $t('exoplatform.perkstore.title.ordersListOf') }} <span class="ml-2 primary--text">{{ selectedProduct.title }}</span>
                <v-text-field
                  v-model="searchOrder"
                  :placeholder="$t('exoplatform.perkstore.label.orderSearchPlaceholder')"
                  prepend-inner-icon="search"
                  single-line
                  hide-details
                  class="searchProductsInput ml-3 mt-1 py-0 d-inline-flex" />
                <v-progress-circular
                  v-show="searchLoading"
                  color="primary"
                  class="mb-2 ma-auto"
                  indeterminate />
              </template>
              <template v-else-if="displayProductOrders && selectedProduct">
                - {{ $t('exoplatform.perkstore.title.myOrdersListOf') }} <span class="ml-2 primary--text">{{ selectedProduct.title }}</span>
              </template>
              <template v-else-if="displayMyOrders">
                - {{ $t('exoplatform.perkstore.title.myOrders') }}
              </template>
              <template v-else-if="perkStoreEnabled && products && products.length">
                <v-text-field
                  v-model="search"
                  :placeholder="$t('exoplatform.perkstore.label.productSearchPlaceholder')"
                  prepend-inner-icon="search"
                  single-line
                  hide-details
                  class="searchProductsInput ml-3 mt-1 py-0 d-inline-flex" />
              </template>
            </v-toolbar-title>
            <v-spacer />
            <template v-if="displayProductOrders">
              <template>
                <v-btn
                  id="perkStoreAppMenuDownloadButton"
                  :title="$t('exoplatform.perkstore.button.exportAsCSV')"
                  icon
                  text
                  @click="exportOrders">
                  <v-icon>
                    fa-download
                  </v-icon>
                </v-btn>
              </template>
            </template>
            <v-btn
              v-if="displayProductDetails && selectedProduct && selectedProduct.userData && selectedProduct.userData.canEdit"
              id="perkStoreAppMenuEditButton"
              :title="$t('exoplatform.perkstore.button.edit')"
              class="primary"
              icon
              text
              dark
              fab
              small
              @click="editProduct(selectedProduct)">
              <v-icon>fa-pen</v-icon>
            </v-btn>
            <v-btn
              v-if="displayCloseIcon"
              id="perkStoreAppMenuCloseButton"
              :title="$t('exoplatform.perkstore.button.close')"
              class="secondary ml-3 mr-3"
              icon
              text
              dark
              fab
              small
              @click="displayProductForm ? displayProduct(selectedProduct) : closeDetails()">
              <v-icon>
                close
              </v-icon>
            </v-btn>
            <template v-else-if="perkStoreEnabled">
              <div id="productFilterMenu">
                <v-menu
                  v-model="productFilterMenu"
                  attach="#productFilterMenu"
                  offset-y>
                  <v-list dense class="pt-0 pb-0">
                    <v-list-item @click="filterProducts">
                      <v-list-item-title>
                        <v-checkbox
                          v-model="productsFilters.disabled"
                          :label="$t('exoplatform.perkstore.label.productFiltersDisabledProducts')"
                          class="pt-0 pb-0" />
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item @click="filterProducts">
                      <v-list-item-title>
                        <v-checkbox
                          v-model="productsFilters.soldOut"
                          :label="$t('exoplatform.perkstore.label.productFiltersSoldOutProducts')"
                          class="pt-0 pb-0" />
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item @click="filterProducts">
                      <v-list-item-title>
                        <v-checkbox
                          v-model="productsFilters.mine"
                          :label="$t('exoplatform.perkstore.label.createdByMe')"
                          class="pt-0 pb-0" />
                      </v-list-item-title>
                    </v-list-item>
                  </v-list>
                </v-menu>
              </div>
              <v-btn
                :title="$t('exoplatform.perkstore.label.productFilters')"
                text
                small
                class="toolbarButton"
                @click="productFilterMenu = true">
                <v-icon
                  :color="productsFilterIconClass"
                  class="mr-2"
                  size="17">
                  fa-filter
                </v-icon>
                <span class="d-sm-inline-flex d-none">
                  {{ $t('exoplatform.perkstore.label.productFilters') }}
                </span>
              </v-btn>
              <span class="d-sm-inline-flex d-none ml-4"></span>
              <v-btn
                id="perkStoreAppMyOrdersButton"
                :title="$t('exoplatform.perkstore.button.myOrders')"
                text
                small
                class="toolbarButton"
                @click="displayMyOrdersList">
                <v-icon class="mr-2" size="17">
                  fa-file-invoice-dollar
                </v-icon>
                <span class="d-sm-inline-flex d-none">
                  {{ $t('exoplatform.perkstore.button.myOrders') }}
                </span>
              </v-btn>
              <v-btn
                v-if="userSettings.canAddProduct"
                id="perkStoreAppMenuAddButton"
                :title="$t('exoplatform.perkstore.button.addProduct')"
                text
                small
                class="toolbarButton d-md-none"
                @click="newProduct()">
                <v-icon size="17">
                  add
                </v-icon>
              </v-btn>
              <template v-if="userSettings.canAddProduct">
                <span class="d-sm-inline-flex d-none ml-3"></span>
                <v-btn
                  v-if="userSettings.administrator"
                  id="perkStoreAppMenuSettingsButton"
                  :title="$t('exoplatform.perkstore.button.settings')"
                  class="toolbarButton"
                  text
                  small
                  @click="displaySettingsModal">
                  <v-icon class="mr-2">
                    fa-cog
                  </v-icon>
                  <span class="d-sm-inline-flex d-none">
                    {{ $t('exoplatform.perkstore.button.settings') }}
                  </span>
                </v-btn>
              </template>
              <div class="primary dark toolbarBalance no-wrap">
                <span class="d-none d-sm-inline-block">
                  {{ $t('exoplatform.perkstore.label.balance') }}:
                </span>
                {{ balance }} {{ symbol }}
              </div>
            </template>
          </v-toolbar>

          <v-toolbar
            v-if="perkStoreEnabled && !walletLoading && walletWarning"
            color="transparent"
            flat>
            <v-spacer />
            <v-flex class="text-center">
              <div class="alert alert-warning">
                <i class="uiIconWarning"></i>
                {{ walletWarning }}
              </div>
            </v-flex>
            <v-spacer />
          </v-toolbar>

          <v-toolbar
            v-if="error"
            color="transparent"
            flat>
            <v-spacer />
            <v-flex class="text-center">
              <div class="alert alert-error">
                <i class="uiIconError"></i> {{ error }}
              </div>
            </v-flex>
            <v-spacer />
          </v-toolbar>

          <orders-list
            v-if="displayProductOrders || displayMyOrders"
            ref="ordersList"
            :product="selectedProduct"
            :selected-order-id="selectedOrderId"
            :orders-filter="ordersFilter"
            :symbol="symbol"
            :search="searchOrder"
            @search-loading="searchLoading = true"
            @end-search-loading="searchLoading = false"
            @init-wallet="initWalletAPI(true)"
            @display-product="displayProduct($event)"
            @loading="loading = $event"
            @error="error = $event"
            @close="closeDetails" />
          <product-form
            v-else-if="displayProductForm"
            ref="productForm"
            :product="selectedProduct"
            @saved="displayProduct"
            @error="error=$event"
            @close="displayProduct" />
          <products-list
            v-else-if="!error || (filteredProducts && filteredProducts.length)"
            ref="productsList"
            :products="filteredProducts"
            :selected-product="displayProductDetails && selectedProduct"
            :symbol="symbol"
            :loading="loading"
            :need-password="walletNeedPassword"
            :can-add-product="userSettings.canAddProduct"
            :wallet-loading="walletLoading"
            :wallet-enabled="walletEnabled && walletAddonInstalled"
            @product-details="displayProduct"
            @create-product="newProduct"
            @orders-list="displayProductOrdersList"
            @edit="editProduct"
            @buy="buyProduct"
            @close="closeDetails" />

          <buy-modal
            ref="buyModal"
            :product="selectedProduct"
            :symbol="symbol"
            :need-password="walletNeedPassword"
            :wallet-loading="walletLoading"
            :wallet-enabled="walletEnabled" />

          <settings-modal
            ref="settingsModal"
            @saved="init()" />

          <product-notification
            :products="modifiedProducts"
            @refresh-list="addNewProductsToList" />
        </v-flex>
      </v-layout>
      <div id="perkStoreDialogsParent">
      </div>
    </main>
    <main v-else-if="!walletLoading" id="applicationDisabled">
      <v-layout wrap class="mt-7">
        <v-flex class="mx-auto text-center title" xs12>
          {{ $t('exoplatform.perkstore.info.applicationDisabledPart1') }}
        </v-flex>
        <v-flex class="mt-2 mx-auto text-center title" xs12>
          {{ $t('exoplatform.perkstore.info.applicationDisabledPart2') }}
        </v-flex>
        <v-flex class="mx-auto text-center title mt-7" xs12>
          <a
            href="https://www.exoplatform.com/rewarding-program"
            target="_blank"
            rel="noopener noreferrer"
            class="no-wrap requestFundsLink">
            {{ $t('exoplatform.perkstore.info.applicationDisabledLink') }}
          </a>
        </v-flex>
      </v-layout>
    </main>
  </v-app>
</template>

<script>
import SettingsModal from './perk-store/SettingsModal.vue';
import ProductsList from './perk-store/ProductsList.vue';
import OrdersList from './perk-store/OrdersList.vue';
import ProductForm from './perk-store/ProductForm.vue';
import BuyModal from './perk-store/BuyModal.vue';
import ProductNotification from './perk-store/ProductNotification.vue';

import {initSettings, getOrderFilter, getProductFilter, storeProductFilter} from '../js/PerkStoreSettings.js';
import {toFixed} from '../js/PerkStoreProductOrder.js';
import {getProductList, getProduct} from '../js/PerkStoreProduct.js';

export default {
  components: {
    SettingsModal,
    ProductsList,
    OrdersList,
    ProductForm,
    BuyModal,
    ProductNotification,
  },
  data: () => ({
    searchOrder: null,
    searchLoading: false,
    productFilterMenu: false,
    walletWarning: null,
    error: null,
    walletAddonInstalled: false,
    walletLoading: false,
    walletEnabled: false,
    perkStoreEnabled: false,
    isApplicationEnabled: false,
    walletNeedPassword: false,
    loading: true,
    selectedProduct: null,
    selectedOrderId: null,
    displayMyOrders: false,
    displayProductDetails: false,
    displayProductForm: false,
    displayProductOrders: false,
    search: null,
    wallet: null,
    contractDetail: null,
    ordersFilter: {},
    productsFilters: {},
    settings: {},
    userSettings: {},
    createOrUpdateOrderEvent: 'exo.perkstore.order.createOrModify',
    createOrUpdateProductEvent: 'exo.perkstore.product.createOrModify',
    products: [],
    modifiedProducts: [],
  }),
  computed: {
    balance() {
      return (this.wallet && this.wallet.tokenBalance && toFixed(this.wallet.tokenBalance)) || 0;
    },
    symbol() {
      return this.contractDetail && this.contractDetail.symbol;
    },
    productsFilterIconClass() {
      return (!this.productsFilters || !this.productsFilters.disabled || !this.productsFilters.soldOut) ? 'primary' : '';
    },
    canEditSelectedProduct() {
      return  this.selectedProduct && this.selectedProduct.userData && this.selectedProduct.userData.canEdit;
    },
    displayFilterButton() {
      return (this.displayProductOrders && !this.selectedOrderId) || this.displayMyOrders;
    },
    displayCloseIcon() {
      return this.displayProductForm || this.displayProductOrders || this.displayProductDetails || this.displayMyOrders;
    },
    filteredProducts() {
      let products = this.products.slice();
      if(this.search && this.search.trim().length) {
        const searchTerm = this.search.trim().toLowerCase();
        products = products.slice().filter(product => (product.title && product.title.toLowerCase().indexOf(searchTerm)) >= 0 || (product.description && product.description.toLowerCase().indexOf(searchTerm) >= 0));
      }
      products = products.filter(product => product.enabled || (product.userData && product.userData.canEdit));
      return products;
    },
  },
  watch: {
    selectedProduct() {
      this.error = null;
    }
  },
  created() {
    document.addEventListener('exo.perkstore.settings.modified', this.refreshSettings);

    document.addEventListener(this.createOrUpdateProductEvent, this.updateProduct);

    document.addEventListener(this.createOrUpdateOrderEvent, this.updateProduct);

    document.addEventListener('exo-wallet-settings-loaded', this.walletSettingsLoaded);
    document.addEventListener('exo-wallet-init-result', this.walletInitialized);
    document.addEventListener('exo-wallet-init-loading', this.walletIsLoading);
    if(window.walletAddonInstalled) {
      this.initWalletAPI();
    } else {
      document.addEventListener('exo-wallet-installed', this.initWalletAPI);
    }
    const search = document.location.search.substring(1);
    let parameters = null;
    if(search) {
      parameters = JSON.parse(
          `{"${decodeURI(search)
            .replace(/"/g, '\\"')
            .replace(/&/g, '","')
            .replace(/=/g, '":"')}"}`
        );
    }
    return this.init(parameters && parameters.productId, parameters && parameters.orderId);
  },
  methods: {
    refreshSettings(event) {
      if(!event || !event.detail || !event.detail.globalsettings) {
        return;
      }

      this.init();
      // refresh entire application if the access permission is changed
    },
    init(selectedProductId, selectedOrderId) {
      this.products = [];
      this.productsFilters = getProductFilter() || {};
      this.loading = true;
      return initSettings()
      .then(() => {
        this.perkStoreEnabled = true;
        this.settings = window.perkStoreSettings;
        if(!this.settings) {
          this.settings = {};
        }
        this.userSettings = this.settings.userSettings;
        this.ordersFilter = getOrderFilter();
      })
      .then(() => this.refreshProductList(selectedProductId, selectedOrderId))
      .catch(e => {
        console.debug("Error initializing application", e);
        this.error = e && e.message ? e.message : String(e);
      })
      .finally(() => {
        window.setTimeout(() => {
          if(window.walletAddonInstalled) {
            this.initWalletAPI();
          } else {
            this.walletWarning = this.$t('exoplatform.perkstore.warning.walletNotInstalled');
            this.loading = false;
          }
        }, 2000);
      });
    },
    filterProducts(event) {
      if (event) {
        event.preventDefault();
        event.stopPropagation();

        storeProductFilter(this.productsFilters);
      }
      this.loading = true;
      return this.refreshProductList().finally(() => this.loading = false);
    },
    refreshProductList(selectedProductId, selectedOrderId) {
      return getProductList()
        .then((products) => {
          this.products = (products && products.filter(product => (!this.productsFilters.mine || (product.creator && product.creator.type === 'user' && product.creator.id === eXo.env.portal.userName)) && (product.enabled || this.productsFilters.disabled) && (product.unlimited || product.totalSupply > product.purchased  || this.productsFilters.soldOut))) || [];

          if (this.products.length && selectedProductId) {
            const selectedProduct = this.products.find(product => product.id === Number(selectedProductId));
            if(selectedProduct) {
              if(selectedOrderId) {
                this.displayProductOrdersList(selectedProduct, Number(selectedOrderId));
              } else {
                this.displayProduct(selectedProduct);
              }
            }
          }
        });
    },
    walletIsLoading() {
      this.walletAddonInstalled = true;
      this.walletLoading = true;
      this.walletWarning = null;
    },
    initWalletAPI(forceReload) {
      if(forceReload || (!this.walletAddonInstalled && window.walletAddonInstalled)) {
        document.dispatchEvent(new CustomEvent('exo-wallet-init'));
      }
    },
    walletSettingsLoaded(event) {
      this.contractDetail = event && event.detail && event.detail.contractDetail;
      this.wallet = event && event.detail && event.detail.wallet;
    },
    walletInitialized(event) {
      const result = event && event.detail;
      if(result && !result.enabled) {
        this.isApplicationEnabled = this.isApplicationEnabled || false;
      } else if(!result || result.error) {
        this.isApplicationEnabled = true;
        this.walletWarning = `${result && result.error ? (`${  result.error}`) : this.$t('exoplatform.perkstore.warning.walletNotConfiguredProperly')}`;
        this.walletEnabled = false;
      } else {
        this.walletEnabled = true;
        this.isApplicationEnabled = this.isApplicationEnabled || result.enabled;
      }
      this.walletNeedPassword = result && result.needPassword;
      this.walletLoading = false;
      this.loading = false;
    },
    closeDetails() {
      this.displayProductDetails = false;
      this.displayProductForm = false;
      this.displayProductOrders = false;
      this.displayMyOrders = false;
      this.selectedProduct = null;
      this.selectedOrderId = 0;
    },
    displayProductOrdersList(product, orderId, currentUserOrders) {
      if (!product) {
        return;
      }
      this.closeDetails();
      this.selectedProduct = product;
      this.selectedOrderId = orderId;
      this.searchOrder = null;
      this.displayProductOrders = true;
      return this.$nextTick().then(() => this.$refs.ordersList && this.$refs.ordersList.init(currentUserOrders));
    },
    exportOrders() {
      return this.$nextTick().then(() => this.$refs.ordersList && this.$refs.ordersList.exportOrders());
    },
    displayMyOrdersList() {
      this.closeDetails();
      this.selectedProduct = null;
      this.displayMyOrders = true;
      return this.$nextTick().then(() => this.$refs.ordersList && this.$refs.ordersList.init());
    },
    newProduct() {
      this.closeDetails();
      this.displayProductForm = true;
      this.selectedProduct = {
        imageFiles: []
      };
      return this.$nextTick().then(() => this.$refs.productForm && this.$refs.productForm.init());
    },
    editProduct(product) {
      this.closeDetails();
      this.displayProductForm = true;
      this.selectedProduct = Object.assign({}, product);
      this.selectedProduct.imageFiles = this.selectedProduct.imageFiles || [];
      return this.$nextTick().then(() => this.$refs.productForm && this.$refs.productForm.init());
    },
    displayProduct(product) {
      if (product) {
        if (product.id) {
          const existingProduct = this.products.find(existingProduct => existingProduct.id === product.id);
          if (existingProduct) {
            product = existingProduct;
          } else {
            this.products.unshift(product);
          }
        } else {
          product = this.products.find(existingProduct => existingProduct.id === product);
        }
      }
      this.closeDetails();
      if (product) {
        this.displayProductDetails = true;
        this.selectedProduct = Object.assign({}, product);
      }
    },
    showFilters() {
      if(this.$refs.ordersList) {
        this.$refs.ordersList.showFilters();
      }
    },
    buyProduct(product) {
      this.selectedProduct = product;
      this.$refs.buyModal.open();
    },
    displaySettingsModal() {
      this.$refs.settingsModal.open();
    },
    updateProduct(event) {
      const wsMessage = event.detail;
      if(wsMessage.product && wsMessage.product.id) {
        let product = this.products && this.products.find(product => product && product.id === wsMessage.product.id);
        // Existing product
        if(product && product.id) {
          getProduct(product.id).then(freshProduct => {
            if(freshProduct && freshProduct.userData && freshProduct.userData.username === eXo.env.portal.userName) {
              // Additional check for user data target
              Object.assign(product, freshProduct);

              if (this.selectedProduct && this.selectedProduct.id === freshProduct.id) {
                Object.assign(this.selectedProduct, freshProduct);
              }
            }
          });
        } else {
          product = wsMessage.product;

          // New product added
          getProduct(product.id).then(freshProduct => {
            if(freshProduct && freshProduct.userData && freshProduct.userData.username === eXo.env.portal.userName) {
              // Additional check for user data target
              Object.assign(product, freshProduct);

              // Add notification to non last modifier
              if(event.type === this.createOrUpdateProductEvent && !this.modifiedProducts.find(modifiedProduct => modifiedProduct.id === product.id) && (product.lastModifier && product.lastModifier.id !== eXo.env.portal.userName) || (!product.lastModifier && product.creator && product.creator.id !== eXo.env.portal.userName)) {
                this.modifiedProducts.unshift(product);
              }
            }
          });
        }
      }
    },
    addNewProductsToList() {
      this.modifiedProducts.reverse().forEach(product => {
        if (!product.lastModifiedDate && !this.products.find(existingProduct => existingProduct.id === product.id)) {
          this.products.unshift(product);
        }
      });
      this.modifiedProducts.splice(0, this.modifiedProducts.length);
    },
  }
};
</script>