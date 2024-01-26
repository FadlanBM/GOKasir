package route

import (
	"github.com/ProjectLSP/internal/controller/Admin"
	"github.com/gofiber/fiber/v2"
)

func AdminRoute(r fiber.Router) {
	app := r.Group("/admin")
	//app.Use(middleware.APIKeyAuthMiddlewareAdmin)
	app.Get("/", Admin.Index)
	app.Post("/", Admin.Create)
	app.Get("/:id", Admin.Detail)
	app.Put("/:id", Admin.Update)
	app.Put("/resetPass/:id", Admin.UpdatePass)
	app.Get("/:id", Admin.Delete)
}
