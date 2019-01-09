package org.exoplatform.addon.perkstore.entity;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import org.hibernate.annotations.DynamicUpdate;

import org.exoplatform.addon.perkstore.model.ProductOrderPeriodType;
import org.exoplatform.commons.api.persistence.ExoEntity;

@Entity(name = "Product")
@ExoEntity
@DynamicUpdate
@Table(name = "ADDONS_PERKSTORE_PRODUCT")
@NamedQueries({ @NamedQuery(name = "Product.getAllProducts", query = "select p from Product ORDER BY p.createdDate DESC") })
public class ProductEntity implements Serializable {

  private static final long       serialVersionUID = -592052513482849972L;

  @Id
  @SequenceGenerator(name = "SEQ_ADDONS_PERKSTORE_PRODUCT_ID", sequenceName = "SEQ_ADDONS_PERKSTORE_PRODUCT_ID")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ADDONS_PERKSTORE_PRODUCT_ID")
  @Column(name = "PRODUCT_ID")
  private Long                    id;

  @Column(name = "TITLE", nullable = false)
  private String                  title;

  @Column(name = "DESCRIPTION", nullable = true)
  private String                  description;

  @Column(name = "ILLUSTRATION_URL", nullable = true)
  private String                  illustrationURL;

  @Column(name = "ENABLED", nullable = false)
  private boolean                 enabled;

  @Column(name = "UNLIMITED", nullable = false)
  private boolean                 unlimited;

  @Column(name = "TOTAL_SUPPLY", nullable = false)
  private double                  totalSupply;

  @Column(name = "PRICE", nullable = false)
  private double                  price;

  @Column(name = "RECEIVER_ID", nullable = false)
  private long                    receiverId;

  @ElementCollection
  @CollectionTable(name = "ADDONS_PERKSTORE_PRODUCT_MARCHAND")
  private List<Long>              marchands;

  @Column(name = "PERIODICITY", nullable = false)
  private ProductOrderPeriodType  orderPeriodicity;

  @Column(name = "MAX_ORDERS_PER_USER", nullable = false)
  private long                    maxOrdersPerUser;

  @Column(name = "CREATED_DATE", nullable = false)
  private long                    createdDate;

  @Column(name = "LAST_MODIFIED_DATE", nullable = true)
  private long                    lastModifiedDate;

  @Column(name = "CREATOR", nullable = false)
  private String                  creator;

  @Column(name = "LAST_MODIFIER", nullable = true)
  private String                  lastModifier;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, orphanRemoval = true)
  private Set<ProductOrderEntity> orders           = new HashSet<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIllustrationURL() {
    return illustrationURL;
  }

  public void setIllustrationURL(String illustrationURL) {
    this.illustrationURL = illustrationURL;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isUnlimited() {
    return unlimited;
  }

  public void setUnlimited(boolean unlimited) {
    this.unlimited = unlimited;
  }

  public double getTotalSupply() {
    return totalSupply;
  }

  public void setTotalSupply(double totalSupply) {
    this.totalSupply = totalSupply;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(long receiverId) {
    this.receiverId = receiverId;
  }

  public List<Long> getMarchands() {
    return marchands;
  }

  public void setMarchands(List<Long> marchands) {
    this.marchands = marchands;
  }

  public ProductOrderPeriodType getOrderPeriodicity() {
    return orderPeriodicity;
  }

  public void setOrderPeriodicity(ProductOrderPeriodType orderPeriodicity) {
    this.orderPeriodicity = orderPeriodicity;
  }

  public long getMaxOrdersPerUser() {
    return maxOrdersPerUser;
  }

  public void setMaxOrdersPerUser(long maxOrdersPerUser) {
    this.maxOrdersPerUser = maxOrdersPerUser;
  }

  public long getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(long createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getLastModifier() {
    return lastModifier;
  }

  public void setLastModifier(String lastModifier) {
    this.lastModifier = lastModifier;
  }

  public long getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(long lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public Set<ProductOrderEntity> getOrders() {
    return orders;
  }

  public void setOrders(Set<ProductOrderEntity> orders) {
    this.orders = orders;
  }

}
