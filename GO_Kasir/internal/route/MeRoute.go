package route

import (
	"github.com/ProjectLSP/internal/controller"
	"github.com/ProjectLSP/internal/middleware"
	"github.com/gofiber/fiber/v2"
)

func MeRoute(c fiber.Router) {
	app := c.Group("/me")
	app.Use(middleware.APIKeyAuthMiddlewareMe)
	app.Get("/", controller.MeController)
}
