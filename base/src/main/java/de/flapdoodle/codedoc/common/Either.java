package de.flapdoodle.codedoc.common;

import org.immutables.value.Value;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Parameter;

import com.google.common.base.Function;

public abstract class Either<L,R> {

    public abstract Either<R, L> swap();

    @Derived
    public L left() {
    	throw new IllegalArgumentException("not left");
    };
    @Derived
    public R right() {
    	throw new IllegalArgumentException("not right");
    }
    
    public abstract boolean isLeft();
    
    public <LX> Either<LX,R> mapLeft(Function<? super L, LX> function) {
    	return isLeft() ? Either.<LX,R>left(function.apply(left())) : Either.<LX,R>right(right());
    }

    public <LX> Either<LX,R> flatmapLeft(Function<? super L, Either<LX,R>> function) {
    	return isLeft() ? function.apply(left()) : Either.<LX,R>right(right());
    }

    public <RX> Either<L,RX> mapRight(Function<? super R, RX> function) {
    	return isLeft() ? Either.<L,RX>left(left()) : Either.<L,RX>right(function.apply(right()));
    }

    public <RX> Either<L,RX> flatmapRight(Function<? super R, Either<L,RX>> function) {
    	return isLeft() ? Either.<L,RX>left(left()) : function.apply(right());
    }

    public static <L,R> Left<L,R> left(L left) {
    	return Left.of(left);
    }
	
    public static <L,R> Right<L,R> right(R right) {
    	return Right.of(right);
    }
	
    @Value.Immutable
	public static abstract class Left<L,R> extends Either<L, R> {

    	@Parameter
    	protected abstract L value();
    	
    	@Override
    	public L left() {
    		return value();
    	}
    	
		@Override
		public Either<R, L> swap() {
			return Right.of(value());
		}

		@Override
		@Derived
		public boolean isLeft() {
			return true;
		}
		
		private static <L,R> Left<L,R> of(L left) {
			return ImmutableLeft.of(left);
		}
	}

    @Value.Immutable
	public static abstract class Right<L,R> extends Either<L, R> {

    	@Parameter
    	protected abstract R value();
    	
    	@Override
    	public R right() {
    		return value();
    	}
    	
		@Override
		public Either<R, L> swap() {
			return Left.of(value());
		}

		@Override
		@Derived
		public boolean isLeft() {
			return true;
		}
		
		private static <L,R> Right<L,R> of(R right) {
			return ImmutableRight.of(right);
		}
	}
}
