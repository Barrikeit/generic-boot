package org.barrikeit.rest.filter;

import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class GenericFilter implements Serializable {}
