package route

import (
	"github.com/ProjectLSP/internal/controller/TokoProfile"
	"github.com/gofiber/fiber/v2"
)

func TokoProfileRoute(c fiber.Router) {
	app := c.Group("/tokoprofile")
	/*	app.Use(middleware.APIKeyAuthMiddlewareAdmin)
	 */app.Get("/", TokoProfile.Index)
	app.Post("/", TokoProfile.Create)
	app.Get("/:id", TokoProfile.Detail)
	app.Put("/:id", TokoProfile.Update)
	app.Delete("/:id", TokoProfile.Delete)
}
