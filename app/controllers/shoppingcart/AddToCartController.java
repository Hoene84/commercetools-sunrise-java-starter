package controllers.shoppingcart;

import com.commercetools.sunrise.framework.components.controllers.PageHeaderControllerComponentSupplier;
import com.commercetools.sunrise.framework.components.controllers.RegisteredComponents;
import com.commercetools.sunrise.framework.controllers.cache.NoCache;
import com.commercetools.sunrise.framework.controllers.metrics.LogMetrics;
import com.commercetools.sunrise.framework.reverserouters.shoppingcart.cart.CartReverseRouter;
import com.commercetools.sunrise.framework.template.TemplateControllerComponentsSupplier;
import com.commercetools.sunrise.framework.template.engine.ContentRenderer;
import com.commercetools.sunrise.sessions.cart.CartDiscountCodesExpansionControllerComponent;
import com.commercetools.sunrise.sessions.cart.CartOperationsControllerComponentSupplier;
import com.commercetools.sunrise.shoppingcart.CartCreator;
import com.commercetools.sunrise.shoppingcart.CartFinder;
import com.commercetools.sunrise.shoppingcart.add.AddToCartControllerAction;
import com.commercetools.sunrise.shoppingcart.add.AddToCartFormData;
import com.commercetools.sunrise.shoppingcart.add.SunriseAddToCartController;
import com.commercetools.sunrise.shoppingcart.content.viewmodels.CartPageContentFactory;
import com.commercetools.sunrise.wishlist.MiniWishlistControllerComponent;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.client.ClientErrorException;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecution;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static io.sphere.sdk.utils.CompletableFutureUtils.recoverWith;

@LogMetrics
@NoCache
@RegisteredComponents({
        TemplateControllerComponentsSupplier.class,
        PageHeaderControllerComponentSupplier.class,
        CartOperationsControllerComponentSupplier.class,
        CartDiscountCodesExpansionControllerComponent.class,
        MiniWishlistControllerComponent.class
})
public final class AddToCartController extends SunriseAddToCartController {

    private final CartReverseRouter cartReverseRouter;

    @Inject
    public AddToCartController(final ContentRenderer contentRenderer,
                               final FormFactory formFactory,
                               final AddToCartFormData formData,
                               final CartFinder cartFinder,
                               final CartCreator cartCreator,
                               final AddToCartControllerAction controllerAction,
                               final CartPageContentFactory pageContentFactory,
                               final CartReverseRouter cartReverseRouter) {
        super(contentRenderer, formFactory, formData, cartFinder, cartCreator, controllerAction, pageContentFactory);
        this.cartReverseRouter = cartReverseRouter;
    }


    public CompletionStage<Result> addArticle(String languageTag, String productId, Integer variantId, Long quantity) {

        final AddLineItem updateAction = AddLineItem.of(productId, variantId, quantity);
        return requireCart(cart -> {
            AddToCartFormData form = new AddToCartFormData() {
                @Override
                public String productId() {
                    return productId;
                }

                @Override
                public Integer variantId() {
                    return variantId;
                }

                @Override
                public Long quantity() {
                    return quantity;
                }
            };
            CompletionStage<Result> result2 = executeAction(cart, form).thenComposeAsync(output -> handleSuccessfulAction(output, form), HttpExecution.defaultContext());
            return recoverWith(result2, t -> result2);
        });
    }

    @Override
    public String getTemplateName() {
        return "cart";
    }

    @Override
    public String getCmsPageKey() {
        return "default";
    }

    @Override
    public CompletionStage<Result> handleSuccessfulAction(final Cart updatedCart, final AddToCartFormData formData) {
        return redirectToCall(cartReverseRouter.cartDetailPageCall());
    }
}
