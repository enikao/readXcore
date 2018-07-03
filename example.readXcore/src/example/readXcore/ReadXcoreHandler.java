package example.readXcore;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class ReadXcoreHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			@SuppressWarnings("unchecked")
			final Iterator<Object> elements = structuredSelection.iterator();

			final ResourceSet resourceSet = new ResourceSetImpl();

			while (elements.hasNext()) {
				final Object element = elements.next();
				if (element instanceof IFile) {
					final IFile file = (IFile) element;

					if ("xcore".equals(file.getFileExtension())) {
						final String fileName = file.getFullPath().toPortableString();
						final URI uri = URI.createURI(fileName);

						resourceSet.getResource(uri, true);
					}
				}
			}

			StreamSupport
			.stream(Spliterators.spliteratorUnknownSize(EcoreUtil.getAllContents(resourceSet, true),
					Spliterator.NONNULL), false)
			.filter(EAttribute.class::isInstance)
			.map(EAttribute.class::cast)
			.forEach(attr -> System.out
					.println("Attribute name: " + attr.getName() + " type: " + attr.getEAttributeType()));
		}
		
		return null;
	}
}
